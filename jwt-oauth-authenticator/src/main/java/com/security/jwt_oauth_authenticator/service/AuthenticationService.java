package com.security.jwt_oauth_authenticator.service;


import com.security.jwt_oauth_authenticator.dto.LoginRequest;
import com.security.jwt_oauth_authenticator.dto.LoginResponse;
import com.security.jwt_oauth_authenticator.dto.RefreshTokenRequest;
import com.security.jwt_oauth_authenticator.dto.RegisterRequest;
import com.security.jwt_oauth_authenticator.entity.Role;
import com.security.jwt_oauth_authenticator.entity.User;
import com.security.jwt_oauth_authenticator.entity.UserRole;
import com.security.jwt_oauth_authenticator.repository.RoleRepository;
import com.security.jwt_oauth_authenticator.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service class that contains the core business logic for
 * user registration, login, and token refreshing.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // --- Dependencies ---
    // We inject all the components we need to perform our logic.
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Handles user registration.
     */
    @Transactional // This ensures all database operations are one transaction
    public LoginResponse register(RegisterRequest request){

        // 1. Check if email already exists
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException(" Email already in use. ");
        }

        // 2. Create the new User entity\
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        // 3. Find the "ROLE_USER"
        // This relies on our DataInitializer to have run successfully.
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_USER not found."));

        // 4. Create the UserRole link
        UserRole userRoleLink = new UserRole();
        userRoleLink.setUser(user);
        userRoleLink.setRole(userRole);
        userRoleLink.setActive(true);

        // 5. Link the UserRole to the User
        // Because of our @OneToMany(cascade = CascadeType.ALL) on the User entity,
        // when we save the User, this UserRole will be saved automatically.
        user.setUserRoles(Set.of(userRoleLink));

        // 6. Save the new User (and their UserRole)
        userRepository.save(user);

        // 7. Generate tokens for the new user and return them (auto-login)
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(accessToken,refreshToken);

    }

    /**
     * Handles user login.
     */
    public LoginResponse login(LoginRequest request){
        // 1. Authenticate the user
        // This is where Spring Security does its magic.
        // It will call our ApplicationUserDetailsService.loadUserByUsername()
        // and then check the password using our PasswordEncoder.
        // If credentials are bad, it throws an AuthenticationException.
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        // 2. If authentication is successful, get the user
        // We can get the UserDetails from the Authentication object...
        // User user = (User) authentication.getPrincipal();

        // ...or just fetch them from the DB (which is often cleaner)
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));

        // 3. Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);

    }

    /**
     * Handles token refreshing.
     * This implements our "Token Rotation" strategy.
     */
    public LoginResponse refresh(RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken();

        // 1. Extract email from the (potentially expired) refresh token
        String email = jwtService.extractUsername(refreshToken);

        // 2. Find the user in the database
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found from refresh token"));

        // 3. Validate the refresh token (check if it's expired, tampered, or not for this user)
        if (!jwtService.isTokenValid(refreshToken, user)) {
            // This will be caught by our exception handler and result in a 401
            throw new SecurityException("Invalid Refresh Token");
        }

        // 4. If valid, generate a NEW access token and a NEW refresh token
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

}
