package com.shop.cart.repository;

import com.shop.cart.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.shop.cart.model.CartItemDetail;
@Repository
public interface CartItemDetailRepository extends JpaRepository<CartItemDetail, Long> {
    CartItemDetail findByItemAndQuantity(Item item, int quantity);

    Optional<CartItemDetail> findByItem(Item item);
}