package com.shop.cart.repository;

import com.shop.cart.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String roleName);

    @Query("SELECT r.name FROM Role r WHERE r.user.id = :user_id")
    List<String> findRoleNamesByUserId(@Param("user_id") Long user_id);
}