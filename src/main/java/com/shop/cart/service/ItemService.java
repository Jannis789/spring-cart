package com.shop.cart.service;

import com.shop.cart.model.Item;
import com.shop.cart.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getProductList() {
        List<Item> products = itemRepository.getAll();
        return products;
    }

    public void createNewItem(String name, int amount, float price) {
        Item newItem = new Item(name, amount, price);
        itemRepository.save(newItem);
    }
}
