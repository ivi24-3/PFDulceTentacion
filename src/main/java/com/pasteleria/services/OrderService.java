package com.pasteleria.services;

import com.pasteleria.models.Order;
import com.pasteleria.models.OrderItem;
import com.pasteleria.models.Product;
import com.pasteleria.models.User;
import com.pasteleria.repositories.OrderRepository;
import com.pasteleria.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductService productService;
    
    public Order createOrder(User user, Map<Long, Integer> cart) {
        BigDecimal total = BigDecimal.ZERO;
        
        Order order = new Order(user, total);
        order = orderRepository.save(order);
        
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            Optional<Product> productOpt = productService.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal itemTotal = product.getPrecio().multiply(new BigDecimal(quantity));
                total = total.add(itemTotal);
                
                OrderItem item = new OrderItem(order, product, quantity, product.getPrecio());
                orderItemRepository.save(item);
                
                // Actualizar stock
                product.setStock(product.getStock() - quantity);
                productService.saveProduct(product);
            }
        }
        
        order.setTotal(total);
        return orderRepository.save(order);
    }
    
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Optional<Order> findByToken(String token) {
        return orderRepository.findByTokenPedido(token);
    }
    
    public Order updateOrderStatus(Long id, String estado) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            String estadoAnterior = order.getEstado();
            order.setEstado(estado);
            
            // Si cambia de PENDIENTE a DESPACHADO, confirmar descuento de stock
            if ("PENDIENTE".equals(estadoAnterior) && "DESPACHADO".equals(estado)) {
                // Stock ya descontado al crear pedido - no hacer nada adicional
            }
            
            // Si cambia de DESPACHADO a PENDIENTE, devolver stock
            if ("DESPACHADO".equals(estadoAnterior) && "PENDIENTE".equals(estado)) {
                for (OrderItem item : order.getItems()) {
                    Product product = item.getProduct();
                    product.setStock(product.getStock() + item.getCantidad());
                    productService.saveProduct(product);
                }
            }
            
            return orderRepository.save(order);
        }
        return null;
    }
    
    public List<Order> searchOrders(String keyword) {
        return orderRepository.searchOrders(keyword);
    }
}