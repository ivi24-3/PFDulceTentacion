package com.pasteleria.controllers;

import com.pasteleria.models.Order;
import com.pasteleria.models.Product;
import com.pasteleria.models.User;
import com.pasteleria.services.OrderService;
import com.pasteleria.services.ProductService;
import com.pasteleria.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user != null) {
            List<Order> orders = orderService.getOrdersByUser(user);
            model.addAttribute("user", user);
            model.addAttribute("orders", orders);
        }
        return "user/dashboard";
    }
    
    @GetMapping("/products")
    public String products(Model model) {
        List<Product> products = productService.getAvailableProducts();
        model.addAttribute("products", products);
        return "user/products";
    }
    
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, 
                           @RequestParam Integer quantity,
                           HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }
        
        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
        session.setAttribute("cart", cart);
        
        return "redirect:/user/products";
    }
    
    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) {
            model.addAttribute("cart", cart);
            model.addAttribute("productService", productService);
        }
        return "user/cart";
    }
    
    @PostMapping("/checkout")
    public String checkout(Authentication auth, HttpSession session) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        
        if (user != null && cart != null && !cart.isEmpty()) {
            orderService.createOrder(user, cart);
            session.removeAttribute("cart");
        }
        
        return "redirect:/user/dashboard";
    }
}