package edu.unimag.order.service;

import edu.unimag.order.model.Order;
import edu.unimag.order.repository.OrderRepository;
import edu.unimag.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    public void setUp() {
        order = Order.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .createdAt(String.valueOf(LocalDateTime.now()))
                .build();
    }

    @Test
    public void testCreateOrder() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order created = orderService.createOrder(order);

        // Assert
        assertThat(created).isNotNull();
        assertThat(created.getTotalAmount()).isEqualTo(100.0);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testGetOrderById() {
        // Arrange
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act
        Optional<Order> found = orderService.getOrderById(order.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTotalAmount()).isEqualTo(100.0);
        verify(orderRepository, times(1)).findById(order.getId());
    }

    @Test
    public void testGetAllOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(order));

        // Act
        List<Order> orders = orderService.getAllOrders();

        // Assert
        assertThat(orders).isNotEmpty();
        assertThat(orders.size()).isEqualTo(1);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateOrder() {
        // Arrange
        when(orderRepository.existsById(order.getId())).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order updated = orderService.updateOrder(order.getId(), order);

        // Assert
        assertThat(updated).isNotNull();
        assertThat(updated.getTotalAmount()).isEqualTo(100.0);
        verify(orderRepository, times(1)).existsById(order.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrder_NotFound() {
        // Arrange
        when(orderRepository.existsById(order.getId())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrder(order.getId(), order);
        });

        assertThat(exception.getMessage()).isEqualTo("Order not found with id: " + order.getId());
        verify(orderRepository, times(1)).existsById(order.getId());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testDeleteOrder() {
        // Arrange
        doNothing().when(orderRepository).deleteById(order.getId());

        // Act
        orderService.deleteOrder(order.getId());

        // Assert
        verify(orderRepository, times(1)).deleteById(order.getId());
    }
}
