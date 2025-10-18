package com.pasteleria.controllers.api;

import com.pasteleria.models.Order;
import com.pasteleria.models.User;
import com.pasteleria.services.OrderService;
import com.pasteleria.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getUserOrders(Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user != null) {
            List<Order> orders = orderService.getOrdersByUser(user);
            return ResponseEntity.ok(orders);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        Optional<Order> order = orderService.findById(id);
        
        if (order.isPresent() && user != null && order.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(order.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/orders/token/{token}")
    public ResponseEntity<Order> getOrderByToken(@PathVariable String token, Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        Optional<Order> order = orderService.findByToken(token);
        
        if (order.isPresent() && user != null && order.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(order.get());
        }
        return ResponseEntity.notFound().build();
    }
}
