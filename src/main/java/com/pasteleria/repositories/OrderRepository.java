package com.pasteleria.repositories;

import com.pasteleria.models.Order;
import com.pasteleria.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByEstado(String estado);
    Optional<Order> findByTokenPedido(String tokenPedido);
    
    @Query("SELECT o FROM Order o WHERE o.user.nombre LIKE %?1% OR o.tokenPedido LIKE %?1%")
    List<Order> searchOrders(String keyword);
}