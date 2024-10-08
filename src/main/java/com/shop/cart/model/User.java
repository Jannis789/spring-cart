package com.shop.cart.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;
    
    // mappedBy: Instanzen vom Set des Types Role sind fremdartig
    @OneToMany(mappedBy = "user")
    private Set<Role> roles = new HashSet<>();

    public User() {}
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addRole(Role role) {
        roles.add(role);
        role.setUser(this);
    }

    // Implementierung von UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean hasRoles(Role role) {
        return this.getRoles().contains(role);
    }
}
