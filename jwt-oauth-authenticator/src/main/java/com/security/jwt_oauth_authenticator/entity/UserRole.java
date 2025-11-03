package com.security.jwt_oauth_authenticator.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserRoleID")
    private Integer userRoleId;

    // This is the "many" side for the User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    // This is the "many" side for the Role
    @ManyToOne(fetch = FetchType.EAGER) // <-- ENSURE this is EAGER
    @JoinColumn(name = "RoleID", nullable = false)
    private Role role;

    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true;

}
