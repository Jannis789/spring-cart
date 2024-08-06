package com.shop.cart.model;

import com.shop.cart.model.Item;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany
    @JoinColumn(name = "cart_item_id")
    private Set<CartItem> cartItem = new HashSet<>();

    private int totalPieces;

    private int totalPrice;
}
