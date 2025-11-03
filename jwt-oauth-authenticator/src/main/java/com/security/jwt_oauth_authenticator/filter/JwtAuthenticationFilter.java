package com.security.jwt_oauth_authenticator.filter;

import com.security.jwt_oauth_authenticator.config.ApplicationUserDetailsService;
import com.security.jwt_oauth_authenticator.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * A custom Spring Security filter that intercepts all requests
 * to validate the JWT Access Token provided in the Authorization header.

 * This is the "gatekeeper" for all secure API endpoints.
 */

@Component // <-- This makes it a Spring Bean, so it can be injected
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {



    private final JwtService jwtService;
    private final ApplicationUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//         1. Check if the request is for an authentication path
        // We don't want to run our filter on the login or register endpoints.
        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response); // Step aside, let it pass
            return;
        }


        // 2. Get the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");

        // 3. Check if header is present and formatted correctly
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Not a JWT, step aside
            return;
        }

        // 4. Extract the token
        final String jwt = authHeader.substring(7); // "Bearer " is 7 chars

        try{
//          5. Extract the user's email from the token
            final String username = jwtService.extractUsername(jwt);

            System.out.println("----------------"+username + " username from the token----------------------------");

            // 6. Check if email exists AND if user is not already authenticated
            // (SecurityContextHolder.getContext().getAuthentication() == null)
            // This prevents the filter from running again if already authenticated
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                // 7. Load the user from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 8. Validate the token against the user details
                if(jwtService.isTokenValid(jwt, userDetails)){
                    // 9. If valid, create the Authentication object
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,    // The "principal"
                            null,           // We don't need credentials
                            userDetails.getAuthorities()); // The user's roles


                    // 10. Set additional details on the token
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 11. This is the MOMENT OF AUTHENTICATION!
                    // We "install" our user into the SecurityContextHolder.
                    // Spring Security will now know this user is authenticated.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        }catch (Exception e){
            System.out.println("Error in JWT filter: " + e.getMessage());
        }

        // 12. Pass the request to the next filter
        filterChain.doFilter(request, response);


    }
}
