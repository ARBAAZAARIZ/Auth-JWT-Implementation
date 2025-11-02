package com.security.jwt_oauth_authenticator.repository;

import com.security.jwt_oauth_authenticator.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    // We'll need this when assigning a default "USER" role
    Optional<Role> findByRoleName(String roleName);

}
