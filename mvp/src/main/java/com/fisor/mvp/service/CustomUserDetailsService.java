package com.fisor.mvp.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fisor.mvp.dto.UserDto;
import com.fisor.mvp.model.Role;
import com.fisor.mvp.model.User;
import com.fisor.mvp.repository.RoleRepository;
import com.fisor.mvp.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not exists by Username or Email"));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map((role) -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                usernameOrEmail,
                user.getPassword(),
                authorities
        );
    }

    public UserDetails saveUser(UserDto userDto){
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        Role userRole = roleRepository.findByName("ROLE_USER");
        User user = new User();
        Set<Role> userRoles = new HashSet<>();
        if(userRole != null){
                userRoles.add(userRole);
        }
        if(optionalUser.isPresent()){
                throw new RuntimeException("User already exists");
        }

        
        
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(userRoles);
        user = userRepository.save(user);
        Set<GrantedAuthority> authorities = user.getRoles().stream()
        .map((role) -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
                
        );

    }

}