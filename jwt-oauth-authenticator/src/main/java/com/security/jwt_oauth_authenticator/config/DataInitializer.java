package com.security.jwt_oauth_authenticator.config;

import com.security.jwt_oauth_authenticator.entity.Role;
import com.security.jwt_oauth_authenticator.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * This component is a CommandLineRunner, which means its 'run' method
 * will be executed automatically by Spring Boot when the application starts.
 * **
 * We use it to ensure our default database roles ("ROLE_USER", "ROLE_ADMIN")
 * exist in the database.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        // --- Create ROLE_USER if it doesn't exist ---
        // By convention, Spring Security roles are prefixed with "ROLE_"
        String userRoleName = "ROLE_USER";

        // We use .findByRoleName() to check if it's already there
        if(roleRepository.findByRoleName(userRoleName).isEmpty()){
            Role role=new Role();
            role.setRoleName(userRoleName);
            role.setDescription("Default role for newly registered user");
            role.setActive(true);
            roleRepository.save(role);
        }

        // --- Create ROLE_ADMIN if it doesn't exist ---
        String adminRoleName = "ROLE_ADMIN";
        if (roleRepository.findByRoleName(adminRoleName).isEmpty()) {
            Role role = new Role();
            role.setRoleName(adminRoleName);
            role.setDescription("Role for administrators");
            role.setActive(true);
            roleRepository.save(role);
            System.out.println("--- Created " + adminRoleName + " ---");
        }


    }
}
