package com.security.jwt_oauth_authenticator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Main security configuration class.
 * This class defines the security rules for HTTP requests,
 * session management, and which endpoints are public or protected.
 */
@Configuration
@EnableWebSecurity  // This enables Spring Security's web security support
public class SecurityConfig {

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        // 1. Disable CSRF protection.
        // We are using stateless JWT authentication, so CSRF is not a concern.
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. Configure URL authorization rules
        http.authorizeHttpRequests(authorize -> authorize
                // --- This is where we define our PUBLIC endpoints ---
                // We create a common path prefix "/api/auth" for all auth-related endpoints
                .requestMatchers("api/auth/**").permitAll()
                // --- All other requests must be authenticated ---
                .anyRequest().authenticated()
        );


        // 3. Configure session management to be STATELESS
        // This is CRITICAL for JWT. It tells Spring Security NOT to create
        // or use any HttpSession. Every request must be authenticated
        // by the token.
        http.sessionManagement(session->session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 4. Disable formLogin
        // As you requested, we disable the default form login.
        http.formLogin(AbstractHttpConfigurer::disable);

        // 5. Enable httpBasic
        //  we keep httpBasic enabled. This is very useful
        // for testing with Postman before we build the JWT filter.
        // Spring Security will use our ApplicationUserDetailsService
        // and PasswordEncoder to validate the Basic Auth credentials.

        http.httpBasic(httpBasic -> {}); // Uses default configuration

        // Build and return the configured HttpSecurity object
        return http.build();
        
    }

}
