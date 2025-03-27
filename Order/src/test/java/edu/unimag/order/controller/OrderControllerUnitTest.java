package edu.unimag.order.controller;

import edu.unimag.order.model.Order;
import edu.unimag.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerUnitTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void testCreateOrder() {
        // Arrange
        Order order = Order.builder()
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();

        when(orderService.createOrder(order)).thenReturn(order);

        // Act
        ResponseEntity<Order> response = orderController.createOrder(order);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(order, response.getBody());
        verify(orderService, times(1)).createOrder(order);
    }

    @Test
    void testGetOrderById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();

        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(order));

        // Act
        ResponseEntity<Order> response = orderController.getOrderById(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testGetOrderByIdNotFound() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderById(orderId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Order> response = orderController.getOrderById(orderId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testGetAllOrders() {
        // Arrange
        Order order1 = Order.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();

        Order order2 = Order.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(200.0)
                .build();

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act
        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testUpdateOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();

        when(orderService.updateOrder(orderId, order)).thenReturn(order);

        // Act
        ResponseEntity<Order> response = orderController.updateOrder(orderId, order);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
        verify(orderService, times(1)).updateOrder(orderId, order);
    }

    @Test
    void testDeleteOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(orderId);
    }
}