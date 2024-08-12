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

    @Transient
    private GrantedAuthority authority;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Role() {}

    public Role(String name) {
        this.name = "ROLE_" + name.toUpperCase();
    }
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

    public GrantedAuthority getAuthority() {
        if (this.authority == null) {
            this.authority = new SimpleGrantedAuthority("ROLE_" + name.toUpperCase());
        }
        return this.authority;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
