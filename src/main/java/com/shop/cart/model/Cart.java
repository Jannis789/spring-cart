package com.shop.cart.model;

import jakarta.persistence.*;
import com.shop.cart.repository.CartRepository;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // CascadeType.REMOVE hinzugefügt, um CartItemDetail beim Löschen des Cart zu entfernen
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "cart_item_id")
    private CartItemDetail cartItemDetail;

    public Cart() {}
    public Cart(User user, CartItemDetail cartItemDetail) {
        this.cartItemDetail = cartItemDetail;
        this.user = user;
    }

    public void setCartItemDetail(CartItemDetail cartItemDetail) {
        this.cartItemDetail = cartItemDetail;
    }
}
