package edu.unimag.order.repository;

import edu.unimag.order.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Test
    void testFindById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> foundOrder = orderRepository.findById(orderId);

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(order.getId(), foundOrder.get().getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testSaveOrder() {
        // Arrange
        Order order = Order.builder()
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();

        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order savedOrder = orderRepository.save(order);

        // Assert
        assertEquals(order.getCustomerId(), savedOrder.getCustomerId());
        assertEquals(order.getTotalAmount(), savedOrder.getTotalAmount());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testDeleteOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        // Act
        orderRepository.deleteById(orderId);

        // Assert
        verify(orderRepository, times(1)).deleteById(orderId);
    }
}