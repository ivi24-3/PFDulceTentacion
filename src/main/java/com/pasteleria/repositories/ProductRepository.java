package com.pasteleria.repositories;

import com.pasteleria.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p WHERE p.nombre LIKE %?1% OR p.descripcion LIKE %?1%")
    List<Product> searchProducts(String keyword);
    
    List<Product> findByStockGreaterThan(Integer stock);
}
