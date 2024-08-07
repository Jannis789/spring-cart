package com.shop.cart.controller;

import com.shop.cart.model.CartItem;
import com.shop.cart.model.Item;
import com.shop.cart.model.User;
import com.shop.cart.model.Role;
import com.shop.cart.repository.ItemRepository;
import com.shop.cart.repository.RoleRepository;
import com.shop.cart.service.ItemService;
import com.shop.cart.service.UserService;
import com.shop.cart.service.RoleService;
import com.shop.cart.service.CartService;

import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.List;
import java.util.Optional;

// Cart (Fremdschlüßel: item_id, user_id; Attribute: quantity)
@Controller
public 
class CartController {
    private float finalPrice = 0;
    private final PasswordEncoder passwordEncoder;
    private final ItemRepository itemRepository;
    private final RoleRepository roleRepository;

    @Autowired 
    private final RoleService roleService;
    
    @Autowired
    private final ItemService itemService;
    
    @Autowired
    private final UserService userService;

    @Autowired final CartService cartService;

    public CartController(
        PasswordEncoder passwordEncoder, 
        ItemRepository itemRepository, 
        RoleRepository roleRepository, 
        ItemService itemService,
        UserService userService,
        RoleService roleService,
        CartService cartService
        ) {
            this.passwordEncoder = passwordEncoder;
            this.itemRepository = itemRepository;
            this.roleRepository = roleRepository;

            this.itemService = itemService;
            this.userService = userService;
            this.roleService = roleService;
            this.cartService = cartService;
    }

    @GetMapping("/addProduct")
    public String showAddProductPage(Model model) {
        User user = this.getLogedInUser();
        if (user == null) {
            return "redirect:/login";
        }

        user = userService.getUserWithRoles(user.getId());
        Role adminRole = roleRepository.getRoleByName("ROLE_ADMIN");

        if (user.hasRoles(adminRole)) {
            return "addProduct";
        }
        return "redirect:/login";
    }
    
    @PostMapping("/addProduct")
    public String addProduct(@RequestParam("item") String itemName,
        @RequestParam("amount") int amount, @RequestParam("price") float price,
        Model model) {
        Item newItem = new Item(itemName, amount, price);
        itemRepository.save(newItem);
        itemService.getProductList().add(newItem);
        return "addProduct";
    }

    @GetMapping("/productPage")
    public String showCartPage(Model model) {
        model.addAttribute("items", itemService.getProductList());
        return "productPage";
    }
    
    @PostMapping("/productPage")
    public String shopProduct(
        @RequestParam("productNames") String[] productNames,
        @RequestParam("quantities") int[] quantities, Model model) {
        int length = productNames.length;

        if (!isLogedIn()) return "redirect:/productPage"; // mit error rumspielen
        
        for (int i = 0; i < length; i++) {
            String productName = productNames[i];
            int quantity = quantities[i];
            if (quantity <= 0) continue;
            Item item = itemRepository.getItemByName(productName);
            cartService.addItemToCart(getLogedInUser(), item, quantity);
        }

        return "redirect:/productPage";
    } 

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
        @RequestParam("userpassword") String userpassword, HttpSession session,
        Model model) {
        Optional<User> optionalUser = userService.getUserByName(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

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
        ServletRequestAttributes attr =
            (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        if (session.getAttribute("user") != null) {
            session.removeAttribute("user");
        }
        return "redirect:/productPage";
    }

    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("price", this.finalPrice);
        return "cart";
    }

    public User getLogedInUser() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        User user = (User) session.getAttribute("user");
        return user;
    }

    public boolean isLogedIn() {
        ServletRequestAttributes attr =
            (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        if (session.getAttribute("user") != null) {
            return true;
        }
        return false;
    }
}