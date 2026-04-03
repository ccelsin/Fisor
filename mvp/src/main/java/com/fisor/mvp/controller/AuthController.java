package com.fisor.mvp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fisor.mvp.dto.JwtAuthResponse;
import com.fisor.mvp.dto.LoginDto;
import com.fisor.mvp.dto.UserDto;
import com.fisor.mvp.exception.CustomException.UnauthorizedException;
import com.fisor.mvp.service.AuthService;
import com.fisor.mvp.service.CustomUserDetailsService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;
    private CustomUserDetailsService userDetailsService;

    // Build Login REST API
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto){
        String token;
        try{
            token = authService.login(loginDto);
        }catch(Exception ex){
            throw new UnauthorizedException(ex.getMessage());
        }

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }
   


    
    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody UserDto userDto){
        userDetailsService.saveUser(userDto);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }
}