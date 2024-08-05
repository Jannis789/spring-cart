package com.shop.cart.controller;

import com.shop.cart.model.Item;
import com.shop.cart.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

@Controller
public class CartController {
    private List<Item> products = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private float finalPrice = 0;
    private final PasswordEncoder passwordEncoder;

    public CartController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        User adminUser = new User("admin", "root");
            adminUser.grantRole("admin");
        User regularUser = new User("user", "password");
        this.users.add(regularUser);
        this.users.add(adminUser);
    }

    // Add Product
    @GetMapping("/addProduct")
    public String showAddProductPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        User user = (User) session.getAttribute("user");
        if (!(user instanceof User)) return "redirect:/login";
        boolean isAdmin = user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return "addProduct";
        return "redirect:/login";
    }

    @PostMapping("/addProduct")
    public String addProduct(@RequestParam("item") String itemName,
                             @RequestParam("amount") int amount,
                             @RequestParam("price") float price,
                             Model model) {
        System.out.println(itemName);
        Item newItem = new Item(itemName, amount, price);
        this.products.add(newItem);         
        return "addProduct";
    }

    // Product Page
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
                                
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        if (session.getAttribute("user") == null) return "redirect:/login";
        
        for (int i = 0; i < quantities.length; i++) {
            for (Item product: this.products) {
                if (product.getName().equals(productNames[i])) {
                    this.finalPrice = Math.min(this.finalPrice + quantities[i] * prices[i], product.getAmount() * prices[i]);
                }
            }
        }
        return "redirect:/productPage";
    }

    // Cart
    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("price", this.finalPrice);
        return "cart";
    }   

    // login 
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("userpassword") String userpassword,
                        HttpSession session, Model model) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (passwordEncoder.matches(userpassword, user.getPassword())) { 
                    session.setAttribute("user", user);
                    return "redirect:/productPage"; 
                }
                break;
            }
        }
        model.addAttribute("error", "Invalid username or password.");
        return "login"; // Return to login page with error message
    }

    @GetMapping("/logout")
    public String getMethodName(Model model) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        if (session.getAttribute("user") != null) session.removeAttribute("user");
        return "redirect:/productPage";
    }
    
}
