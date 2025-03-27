package edu.unimag.order.service;

import edu.unimag.order.model.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    Order createOrder(Order order);
    Optional<Order> getOrderById(UUID id);
    List<Order> getAllOrders();
    Order updateOrder(UUID id, Order order);
    void deleteOrder(UUID id);
}
