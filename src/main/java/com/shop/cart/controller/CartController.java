package com.shop.cart.controller;
import com.shop.cart.model.Item;
import com.shop.cart.model.User;
import com.shop.cart.repository.UserRepository;
import com.shop.cart.repository.ItemRepository;
import com.shop.cart.repository.RoleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Cart (Fremdschlüßel: item_id, user_id; Attribute: quantity)
@Controller
public 
class CartController {
    private List<Item> products = new ArrayList<>();
    private float finalPrice = 0;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RoleRepository roleRepository;
    public CartController(PasswordEncoder passwordEncoder,
        UserRepository userRepository, ItemRepository itemRepository,
        RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.roleRepository = roleRepository;
    }
    @GetMapping("/addProduct")
    public String showAddProductPage(Model model) {
        ServletRequestAttributes attr =
            (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<String> userRoles =
            roleRepository.findRoleNamesByUserId(user.getId());
        System.out.println(userRoles);
        if (userRoles.stream().anyMatch(role -> role.equals("ROLE_ADMIN"))) {
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
        this.products.add(newItem);
        return "addProduct";
    }
    @GetMapping("/productPage")
    public String showCartPage(Model model) {
        model.addAttribute("items", products);
        return "productPage";
    }
    @PostMapping("/productPage")
    public String shopProduct(
        @RequestParam("productNames") String[] productNames,
        @RequestParam("quantities") int[] quantities,
        @RequestParam("prices") float[] prices, Model model) {
        for (int i = 0; i < quantities.length; i++) {
            for (Item product : this.products) {
                if (product.getName().equals(productNames[i])) {
                    this.finalPrice =
                        Math.min(this.finalPrice + quantities[i] * prices[i],
                            product.getAmount() * prices[i]);
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
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
        @RequestParam("userpassword") String userpassword, HttpSession session,
        Model model) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            System.out.println("Eingabe: " + userpassword
                + " | DB Password:" + user.getPassword() + " | value: "
                + passwordEncoder.matches(userpassword, user.getPassword()));
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
}