package com.pasteleria.controllers;

import com.pasteleria.models.Order;
import com.pasteleria.models.Product;
import com.pasteleria.models.User;
import com.pasteleria.services.OrderService;
import com.pasteleria.services.ProductService;
import com.pasteleria.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @GetMapping("/products")
    public String products(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/products";
    }
    
    @GetMapping("/products/new")
    public String newProduct() {
        return "admin/product-form";
    }
    
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("editing", true);
            return "admin/product-form";
        }
        return "redirect:/admin/products";
    }
    
    @PostMapping("/products")
    public String saveProduct(@RequestParam String nombre,
                             @RequestParam String descripcion,
                             @RequestParam BigDecimal precio,
                             @RequestParam Integer stock) {
        Product product = new Product(nombre, descripcion, precio, stock);
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }
    
    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
                               @RequestParam String nombre,
                               @RequestParam String descripcion,
                               @RequestParam BigDecimal precio,
                               @RequestParam Integer stock) {
        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setNombre(nombre);
            product.setDescripcion(descripcion);
            product.setPrecio(precio);
            product.setStock(stock);
            productService.saveProduct(product);
        }
        return "redirect:/admin/products";
    }
    
    @GetMapping("/orders")
    public String orders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }
    
    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String estado) {
        orderService.updateOrderStatus(id, estado);
        return "redirect:/admin/orders";
    }
}