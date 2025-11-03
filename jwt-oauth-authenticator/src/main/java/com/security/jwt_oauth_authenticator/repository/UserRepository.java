package com.security.jwt_oauth_authenticator.repository;

import com.security.jwt_oauth_authenticator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    // This method will be used by our UserDetailsService
    // It will find a user by their email (which we use as the 'username')
    Optional<User> findByEmail(String email);

    // This will be useful for our OAuth flow
    boolean existsByEmail(String email);

    // "SELECT * FROM users WHERE Username = ? OR Email = ?"
    Optional<User> findByUsernameOrEmail(String username, String email);



}
