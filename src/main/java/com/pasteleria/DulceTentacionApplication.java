// PasteleriaApplication.java
package com.pasteleria;

import com.pasteleria.models.Product;
import com.pasteleria.models.User;
import com.pasteleria.repositories.ProductRepository;
import com.pasteleria.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;

@SpringBootApplication
public class DulceTentacionApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(DulceTentacionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear admin por defecto si no existe
        if (userRepository.findByEmail("admin@pasteleria.com").isEmpty()) {
            User admin = new User();
            admin.setDni("12345678");
            admin.setNombre("Administrador");
            admin.setEmail("admin@pasteleria.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setToken("ADMIN_TOKEN_" + System.currentTimeMillis());
            userRepository.save(admin);
            System.out.println("Admin creado: admin@pasteleria.com / admin123");
        }

        // Crear productos de ejemplo si no existen
        if (productRepository.count() == 0) {
            Product[] productos = {
                new Product("Torta de Chocolate", "Deliciosa torta de chocolate con crema", new BigDecimal("45.00"), 10),
                new Product("Cupcakes de Vainilla", "Set de 6 cupcakes de vainilla", new BigDecimal("25.00"), 15),
                new Product("Pie de Limón", "Refrescante pie de limón", new BigDecimal("35.00"), 8),
                new Product("Cookies Choco Chip", "Galletas con chispas de chocolate (docena)", new BigDecimal("18.00"), 20),
                new Product("Cheesecake de Fresa", "Cremoso cheesecake con fresas", new BigDecimal("42.00"), 6)
            };
            
            for (Product producto : productos) {
                productRepository.save(producto);
            }
            
            System.out.println("Productos de ejemplo creados");
        }
    }
}
