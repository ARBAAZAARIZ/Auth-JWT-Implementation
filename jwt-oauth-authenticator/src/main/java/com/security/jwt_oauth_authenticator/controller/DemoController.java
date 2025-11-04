package com.security.jwt_oauth_authenticator.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {

    /* **
     * This endpoint is secured by our SecurityConfig.
     * The JwtAuthenticationFilter will run, validate the token,
     * and populate the SecurityContextHolder.

     * We can then access the authenticated user's details.
     */

    @GetMapping
    public ResponseEntity<String> demo(){
        // Get the authenticated user from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // We can also get their roles (authorities)
                String roles = authentication.getAuthorities().toString();

        return ResponseEntity.ok("Hello " + username + "! Your token is valid. Your roles are:" + roles);
    }
}
