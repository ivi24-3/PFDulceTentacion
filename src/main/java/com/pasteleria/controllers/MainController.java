package com.pasteleria.controllers;

import com.pasteleria.models.Product;
import com.pasteleria.models.User;
import com.pasteleria.services.ProductService;
import com.pasteleria.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class MainController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = productService.getAvailableProducts();
        model.addAttribute("products", products);
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String dni, 
                              @RequestParam String nombre,
                              @RequestParam String email, 
                              @RequestParam String password) {
        try {
            // Usar el nombre obtenido de la API RENIEC
            userService.registerUser(dni, nombre, email, password);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            return "redirect:/register?error=true";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user != null && "ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/user/dashboard";
    }
}