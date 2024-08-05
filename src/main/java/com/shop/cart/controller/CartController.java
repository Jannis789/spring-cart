package com.shop.cart.controller;

import com.shop.cart.model.Item;
import com.shop.cart.model.User;
import com.shop.cart.model.Role;
import com.shop.cart.repository.UserRepository;
import com.shop.cart.repository.ItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {
    private List<Item> products = new ArrayList<>();
    private float finalPrice = 0;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public CartController(PasswordEncoder passwordEncoder, UserRepository userRepository, ItemRepository itemRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        // Adding admin and regular users to the database
        // createUserIfNotExists("admin", "root", "admin");
        // createUserIfNotExists("testUser", "testPassword", "user");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
        }
    }

    private void createUserIfNotExists(String username, String password, String role) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User(username, passwordEncoder.encode(password));
            Role adminRole = new Role(role, user);
            user.addRole(adminRole);
            userRepository.save(user);
        }
        System.out.println(userRepository.findByUsername("testUser"));
    }

    // Add Product
    @GetMapping("/addProduct")
    public String showAddProductPage(Model model) {
        // Admin Authentifizierung muss noch nachgemacht werden s
        return "addProduct";

    }
    

    @PostMapping("/addProduct")
    public String addProduct(@RequestParam("item") String itemName,
                             @RequestParam("amount") int amount,
                             @RequestParam("price") float price,
                             Model model) {
        Item newItem = new Item(itemName, amount, price);
        itemRepository.save(newItem);
        this.products.add(newItem);
        return "addProduct";
    }

    // Product Page

    @GetMapping("/productPage")
    public String showCartPage(Model model) {
        model.addAttribute("items", products);
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
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            System.out.println("Eingabe: " + userpassword  + " | DB Password:" + user.getPassword() + " | value: " + passwordEncoder.matches(userpassword, user.getPassword()));
            if (passwordEncoder.matches(userpassword, user.getPassword())) { 
                session.setAttribute("user", user);
                return "redirect:/productPage"; 
            }
        }
        model.addAttribute("error", "Invalid username or password.");
        return "login"; 
    }
    

    @GetMapping("/logout")
    public String logout(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return "redirect:/productPage";
    }
}
