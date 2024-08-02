package com.shop.cart.controller;

import com.shop.cart.model.Item;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

@Controller
public class CartController {

    private List<Item> products = new ArrayList<>();
    private float finalPrice = 0;

    @GetMapping("/addProduct")
    public String showAddProductPage(Model model) {
        return "addProduct";
    }

    @PostMapping("/addProduct")
    public String addProduct(@RequestParam("item") String itemName,
                             @RequestParam("amount") int amount,
                             @RequestParam("price") float price,
                             Model model) {
        Item newItem = new Item(itemName, amount, price);
        this.products.add(newItem);         
        return "addProduct";
    }


    @GetMapping("/productPage")
    public String showCartPage(Model model) {
        model.addAttribute("items", products); // Füge die Liste von Items dem Modell hinzu
        return "productPage";
    }

    @PostMapping("/productPage")
    public String shopProduct(@RequestParam("productNames") String[] productNames, 
                              @RequestParam("quantities") int[] quantities, 
                              @RequestParam("prices") float[] prices,
                              Model model) {
        for (int i = 0; i < quantities.length; i++) {
            for (Item product: this.products) {
                if (product.getName().equals(productNames[i])) {
                    this.finalPrice = Math.min(this.finalPrice + quantities[i] * prices[i], product.getAmount() * prices[i]);
                }
            }
        }

        return "redirect:/productPage";
    }
    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("price", this.finalPrice);
        return "cart";
    }   
}
