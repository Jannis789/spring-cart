package com.shop.cart.repository;

import com.shop.cart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // macht das was es sagt
// Namenskonventin -> findBy<propertyname bzw. columnname>
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
