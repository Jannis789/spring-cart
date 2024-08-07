package com.shop.cart.repository;

import com.shop.cart.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i")
    List<Item> getAll();

    @Query("SELECT i FROM Item i WHERE i.name = :name")
    Item getItemByName(@Param("name") String name);
}
