package com.security.jwt_oauth_authenticator.config;

import com.security.jwt_oauth_authenticator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {



    // injecting our user repository
    private final UserRepository userRepository;





    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Because our `User` entity implements `UserDetails`, we can just return
        // the User object directly. Spring Security knows how to handle it.
    }
}
