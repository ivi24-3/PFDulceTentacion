package com.pasteleria.services;

import com.pasteleria.models.User;
import com.pasteleria.models.Order;
import com.pasteleria.models.OrderItem;
import com.pasteleria.models.Product;
import com.pasteleria.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;
    
    public User registerUser(String dni, String nombre, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        
        User user = new User(dni, nombre, email, encodedPassword);
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByToken(String token) {
        return userRepository.findByToken(token);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }
    
    public void deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Restaurar stock de pedidos pendientes antes de eliminar
            List<Order> orders = orderService.getOrdersByUser(user);
            for (Order order : orders) {
                if ("PENDIENTE".equals(order.getEstado())) {
                    for (OrderItem item : order.getItems()) {
                        Product product = item.getProduct();
                        product.setStock(product.getStock() + item.getCantidad());
                        productService.saveProduct(product);
                    }
                }
            }
        }
        
        // El cascade eliminará automáticamente orders y orderItems
        userRepository.deleteById(id);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByDni(String dni) {
        return userRepository.findByDni(dni);
    }
}