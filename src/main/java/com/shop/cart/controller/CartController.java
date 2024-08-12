package com.shop.cart.controller;

import com.shop.cart.model.Item;
import com.shop.cart.model.User;
import com.shop.cart.model.Role;
import com.shop.cart.model.Cart;
import com.shop.cart.model.CartItemDetail;
import com.shop.cart.repository.CartItemDetailRepository;
import com.shop.cart.repository.CartRepository;
import com.shop.cart.repository.ItemRepository;
import com.shop.cart.repository.RoleRepository;
import com.shop.cart.service.ItemService;
import com.shop.cart.service.UserService;
import com.shop.cart.service.RoleService;
import com.shop.cart.service.CartService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Optional;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


// Cart (Fremdschlüßel: item_id, user_id; Attribute: quantity)
@Controller
public 
class CartController {
    private final PasswordEncoder passwordEncoder;
    private final ItemRepository itemRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final CartItemDetailRepository cartItemDetailRepository;

    @Autowired 
    private final RoleService roleService;
    
    @Autowired
    private final ItemService itemService;
    
    @Autowired
    private final UserService userService;

    @Autowired 
    private final CartService cartService;

    public CartController(
        PasswordEncoder passwordEncoder, 
        ItemRepository itemRepository, 
        RoleRepository roleRepository, 
        ItemService itemService,
        CartRepository cartRepository,
        CartItemDetailRepository cartItemDetailRepository,
        UserService userService,
        RoleService roleService,
        CartService cartService
        ) {
            this.passwordEncoder = passwordEncoder;
            this.itemRepository = itemRepository;
            this.roleRepository = roleRepository;
            this.cartRepository = cartRepository;
            this.cartItemDetailRepository = cartItemDetailRepository;

            this.itemService = itemService;
            this.userService = userService;
            this.roleService = roleService;
            this.cartService = cartService;
            userService.createUserIfNotExists("admin", "root", "ADMIN", passwordEncoder);
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
        @RequestParam("amount") int amount, @RequestParam("price") float price) {
        Item newItem = new Item(itemName, amount, price);
        itemRepository.save(newItem);
        itemService.getProductList().add(newItem);
        return "addProduct";
    }

    @GetMapping("/productPage")
    public String showProductPage(
        @RequestParam(value = "productNames", required = false) List<String> productNames,
        @RequestParam(value = "quantities", required = false) List<Integer> quantities,
        Model model) {
    
        if (!isLogedIn()) return "redirect:/login"; // redirect to login page if not logged in
    
        // Wenn keine Parameter übergeben wurden, setze Default-Werte oder mache nichts
        if (productNames != null && quantities != null) {
            int length = productNames.size();
            for (int i = 0; i < length; i++) {
                String productName = productNames.get(i);
                int quantity = quantities.get(i);
                if (quantity <= 0) continue;
                Item item = itemRepository.getItemByName(productName);
                if (item != null) {
                    cartService.createOrUpdateCart(getLogedInUser(), item, quantity);
                } else {
                    System.out.println("Item not found: " + productName);
                }
            }
        }
    
        // Fetch the product list for display
        model.addAttribute("items", itemService.getProductList());
    
        return "productPage";
    }
    
    @PostMapping("/productPage")
    public String shopProduct(
        @RequestParam("productNames") List<String> productNames,
        @RequestParam("quantities") List<Integer> quantities, 
        Model model) {
    
        if (!isLogedIn()) return "redirect:/productPage";
    
        int length = productNames.size();
        for (int i = 0; i < length; i++) {
            String productName = productNames.get(i);
            int quantity = quantities.get(i);
            if (quantity <= 0) continue;
            Item item = itemRepository.getItemByName(productName);
            if (item != null) {
                cartService.createOrUpdateCart(getLogedInUser(), item, quantity);
            } else {
                System.out.println("Item not found: " + productName);
            }
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
                return "redirect:/productPage"; // Umleitung nach erfolgreichem Login
            }
        }
        model.addAttribute("error", "Invalid username or password.");
        return "login"; // Zeigt die Login-Seite bei fehlerhaften Anmeldeversuchen an
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
        List<CartItemDetail> cartItemDetails =  cartRepository.findCartItemDetailsByUser(getLogedInUser());


        model.addAttribute("price", cartService.getPriceForUser(getLogedInUser()));
        if (isLogedIn()) model.addAttribute("cartItemDetails",cartItemDetails);
        return "cart";
    }

    @PostMapping("/updateQuantities")
    public String updateQuantities(
        @RequestParam("name") String name,
        @RequestParam("quantity") int quantity,
        Model model) {
        if (!isLogedIn()) return "redirect:/cart";
        Item item = itemRepository.getItemByName(name);
        Optional<CartItemDetail> cartItemDetail = cartItemDetailRepository.findByItem(item);
        if (cartItemDetail.isPresent()) {
            cartItemDetail.get().setQuantity(quantity);
            cartItemDetailRepository.save(cartItemDetail.get());
        }
        return "redirect:/cart";
    }

    @GetMapping("/deleteProduct")
    public String deleteProduct(@RequestParam("name") String name, Model model) {
        if (!isLogedIn() || name == null) return "redirect:/cart";

        cartService.deleteProduct(name);

        return "redirect:/cart";
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