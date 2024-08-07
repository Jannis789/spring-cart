package com.shop.cart.repository;

import com.shop.cart.model.Item;
import com.shop.cart.model.User;
import com.shop.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUserAndItem(User user, Item item);
}

