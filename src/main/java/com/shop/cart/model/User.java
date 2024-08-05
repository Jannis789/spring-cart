package com.shop.cart.model;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class User implements UserDetails {
    private String username;
    private String password;
    private Set<String> roles = new HashSet<>();

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User(String username, String password) {
        this.username = username;
        this.password = passwordEncoder.encode(password);
    }

    public void grantRole(String role) {
        this.roles.add(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        }
        return authorities;
    }

}
