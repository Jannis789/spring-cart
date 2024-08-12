package com.shop.cart.service;

import com.shop.cart.model.Cart;
import com.shop.cart.model.CartItemDetail;
import com.shop.cart.repository.ItemRepository;
import com.shop.cart.model.Item;
import com.shop.cart.model.User;
import com.shop.cart.repository.CartRepository;
import com.shop.cart.repository.CartItemDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemDetailRepository cartItemDetailRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public void createOrUpdateCart(User user, Item item, int quantity) {

        Optional<CartItemDetail> existingCartItemDetail = cartItemDetailRepository.findByItem(item);
        if (existingCartItemDetail.isPresent()) {
            int newQuantity = Math.min(existingCartItemDetail.get().getQuantity() + quantity, existingCartItemDetail.get().getItem().getAmount());
            existingCartItemDetail.get().setQuantity(newQuantity);
            return;
        }

        CartItemDetail cartItemDetail = new CartItemDetail(item, quantity);
        cartItemDetailRepository.save(cartItemDetail);

        Cart cart = new Cart(user, cartItemDetail);
        cartRepository.save(cart);
    }

    @Transactional
    public void deleteProduct(String name) {
        Item item = itemRepository.getItemByName(name);
        Optional<CartItemDetail> cid = cartItemDetailRepository.findByItem(item);

        if (cid.isPresent()) {
            cartRepository.deleteByCartItemDetail(cid.get());
        }
    }

    public float getPriceForUser(User user) {
        float price = 0;
        List<CartItemDetail> cartItemDetails = cartRepository.findCartItemDetailsByUser(user);
        for (CartItemDetail detail : cartItemDetails) {
            price += detail.getItem().getPrice() * detail.getQuantity();
        }
        return price;
    }
}

