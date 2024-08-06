package com.shop.cart.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private GrantedAuthority authority;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    // Getter und Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GrantedAuthority getAuthority() {
        if (this.authority == null) {
            this.authority = new SimpleGrantedAuthority("ROLE_" + name.toUpperCase());
        }
        return this.authority;
    }
}
