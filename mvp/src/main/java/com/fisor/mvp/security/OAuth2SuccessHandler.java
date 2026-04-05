package com.fisor.mvp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fisor.mvp.model.Role;
import com.fisor.mvp.model.User;
import com.fisor.mvp.repository.RoleRepository;
import com.fisor.mvp.repository.UserRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Récupérer les infos de Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Vérifier si l'user existe déjà en BD
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(email, email);
        User user;

        if (optionalUser.isEmpty()) {
            // Créer l'user s'il n'existe pas
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setPassword(UUID.randomUUID().toString()); // Générer un mot de passe aléatoire pour ne pas laisser le champs vide
            
            // Assigner le rôle USER par défaut
            Role userRole = roleRepository.findByName("ROLE_USER");
            Set<Role> userRoles = new HashSet<>();
            if (userRole != null) {
                userRoles.add(userRole);
            }
            user.setRoles(userRoles);
            user = userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        // Créer une Authentication avec l'user en BD pour générer le JWT
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                authorities
        );

        // Générer le JWT
        String jwt = jwtTokenProvider.generateToken(newAuth);

        // Rediriger avec le JWT 
        response.sendRedirect("http://localhost:3000/auth/callback?token=" + jwt);
    }
}
