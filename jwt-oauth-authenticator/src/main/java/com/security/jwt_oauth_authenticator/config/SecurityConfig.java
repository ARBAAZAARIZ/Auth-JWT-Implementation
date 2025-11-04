package com.security.jwt_oauth_authenticator.config;

import com.security.jwt_oauth_authenticator.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Main security configuration class.
 * This class defines the security rules for HTTP requests,
 * session management, and which endpoints are public or protected.
 */
@Configuration
@EnableWebSecurity  // This enables Spring Security's web security support
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        // 1. Disable CSRF protection.
        // We are using stateless JWT authentication, so CSRF is not a concern.
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. Configure URL authorization rules
        http.authorizeHttpRequests(authorize -> authorize
                // --- This is where we define our PUBLIC endpoints ---
                // We create a common path prefix "/api/auth" for all auth-related endpoints
                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers("/api/v1/demo").hasRole("USER")// Only users with ROLE_USER
                // --- All other requests must be authenticated ---
                .anyRequest().authenticated()
        );

        http.cors(Customizer.withDefaults());


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

        // --- NEW: Add the JwtAuthenticationFilter ---
        // This is the most important line:
        // We tell Spring Security to use our 'jwtAuthFilter'
        // and to run it BEFORE the standard UsernamePasswordAuthenticationFilter.
        // This ensures our token is checked *before* Spring tries to find a
        // username/password.
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        // Build and return the configured HttpSecurity object
        return http.build();
        
    }

    // --- THIS BEAN IS NOW MORE SECURE ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from our Angular app
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allow all standard methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers (including Authorization and our X-Refresh-Token)
        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // --- THIS IS THE FIX ---
        // We only apply this CORS configuration to paths starting with /api/
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

}
