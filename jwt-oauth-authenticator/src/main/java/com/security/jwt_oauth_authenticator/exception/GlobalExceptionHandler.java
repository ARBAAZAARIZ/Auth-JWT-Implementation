package com.security.jwt_oauth_authenticator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.AuthenticationException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles bad login credentials (e.g., wrong password).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String,String>> handleAuthenticationException(AuthenticationException ex){
        // This is thrown by AuthenticationManager on bad credentials
        return new ResponseEntity<>(Map.of("error","Invalid username or password"), HttpStatus.UNAUTHORIZED); // 401
    }

    /*
     * Handles user not found errors (e.g., from refresh token).
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleUsernameNotFoundException(UsernameNotFoundException ex){
        return new ResponseEntity<>(
                Map.of("error",ex.getMessage()),
                HttpStatus.NOT_FOUND); // 404
    }

    /*
     * Handles registration with an email that is already in use.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        // This is the exception we threw in AuthenticationService
        return new ResponseEntity<>(
                Map.of("error", ex.getMessage()),
                HttpStatus.BAD_REQUEST // 400
        );
    }

    /**
     * Handles invalid refresh tokens.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex) {
        // This is the exception we threw for an invalid refresh token
        return new ResponseEntity<>(
                Map.of("error", ex.getMessage()),
                HttpStatus.UNAUTHORIZED // 401
        );
    }


}
