package com.security.jwt_oauth_authenticator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID")
    private Integer roleId;

    @Column(name = "RoleName", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true;

    // This defines the "many" side of the relationship
    // One Role can be associated with many UserRole entries
    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles;
}
