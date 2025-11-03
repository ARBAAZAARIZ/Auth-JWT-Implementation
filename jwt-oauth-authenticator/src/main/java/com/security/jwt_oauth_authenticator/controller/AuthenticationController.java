package com.security.jwt_oauth_authenticator.controller;

import com.security.jwt_oauth_authenticator.dto.LoginRequest;
import com.security.jwt_oauth_authenticator.dto.LoginResponse;
import com.security.jwt_oauth_authenticator.dto.RefreshTokenRequest;
import com.security.jwt_oauth_authenticator.dto.RegisterRequest;
import com.security.jwt_oauth_authenticator.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for all authentication-related endpoints.
 * This class delegates all logic to the AuthenticationService.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    // The service handles all logic: creating user, hashing, saving,
    // and generating tokens.
    private final AuthenticationService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
            ){
        // The service handles all logic: validating the refresh token,
        // and generating a new set of tokens (rotation).
        return ResponseEntity.ok(authService.register(request));
    }


    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ){
        // The service handles all logic: creating user, hashing, saving,
        // and generating tokens.
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestBody RefreshTokenRequest request
    ){
        // The service handles all logic: creating user, hashing, saving,
        // and generating tokens.
        return ResponseEntity.ok(authService.refresh(request));
    }





}
