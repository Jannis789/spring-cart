package com.shop.cart.service;
import com.shop.cart.repository.CartItemRepository;
import com.shop.cart.model.CartItem;
import com.shop.cart.model.Item;
import com.shop.cart.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.Math;
@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    public void addItemToCart(User user, Item item, int quantity) {
        if (user == null || item == null) {
            throw new IllegalArgumentException("User and Item must not be null");
        }

        CartItem cartItem = cartItemRepository.findByUserAndItem(user, item);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem(item, user, quantity);
        }

        cartItemRepository.save(cartItem);
    }
}

