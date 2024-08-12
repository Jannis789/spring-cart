package com.shop.cart.repository;

import com.shop.cart.model.Cart;
import com.shop.cart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.shop.cart.model.CartItemDetail;
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c.cartItemDetail FROM Cart c WHERE c.user = :user")
    List<CartItemDetail> findCartItemDetailsByUser(@Param("user") User user);

    @Query("SELECT c.cartItemDetail.id FROM Cart c WHERE c.user = :user")
    List<Long> findCartItemDetailsIdsByUser(@Param("user") User user);

    @Query("SELECT c.user FROM Cart c WHERE c = :cart")
    User findUserByCart(@Param("cart") Cart cart);

    @Query("SELECT c.user.id FROM Cart c WHERE c = :cart")
    User findUserIdByCart(@Param("cart") Cart cart);

    Cart findByUser(User user);

    List<Cart> findByCartItemDetail(CartItemDetail cartItemDetail);
    void deleteByCartItemDetail(CartItemDetail cartItemDetail);
}
