package com.security.jwt_oauth_authenticator.config;

import com.security.jwt_oauth_authenticator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


                UserDetails  user=userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Because our `User` entity implements `UserDetails`, we can just return
        // the User object directly. Spring Security knows how to handle it.

        System.out.println(user.getUsername() + " from application details service class");
        System.out.println(user.getPassword() + " from application details service class");
        return user;

    }
}
