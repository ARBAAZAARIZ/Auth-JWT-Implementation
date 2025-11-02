package com.security.jwt_oauth_authenticator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    /**
     * This is the bean you wanted to "right manually". This is the central
     * component that manages authentication. We need to expose it as a bean
     * so we can inject it into our custom Login Controller later.
     *
     * Spring Boot will automatically configure this manager to use our
     * ApplicationUserDetailsService and PasswordEncoder beans.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

//      Spring Boot's autoconfiguration will find this bean.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
