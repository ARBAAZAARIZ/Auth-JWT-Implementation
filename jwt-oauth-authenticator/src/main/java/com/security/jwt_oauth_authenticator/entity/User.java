package com.security.jwt_oauth_authenticator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Username", nullable = false, unique = true, length = 50)
    private String username;


    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "PasswordHash", nullable = false, length = 255)
    private String password; // We'll name the field 'password' in Java

    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true;


    // This is the "one" side of the relationship
    // We MUST use EAGER fetching. When we load a User for login,
    // we need their roles immediately.
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // This is where we map our roles
        // We stream the UserRole set, filter for active ones,
        // and map them to Spring Security's SimpleGrantedAuthority
        return userRoles.stream()
                .filter(UserRole::isActive)
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // --- We can use the 'IsActive' field for these ---

    @Override
    public boolean isAccountNonExpired() {
        // Or you can just return true if account expiry is not a feature
        return this.isActive;
    }
    @Override
    public boolean isAccountNonLocked() {
        return this.isActive;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return this.isActive;
    }
    @Override
    public boolean isEnabled() {
        // This is the most important one
        return this.isActive;
    }



}
