package edu.unimag.order.controller;

import edu.unimag.order.model.Order;
import edu.unimag.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class OrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private Order order;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll(); // Limpia la base de datos antes de cada prueba
        order = Order.builder()
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();
        order = orderRepository.save(order);
    }

    @Test
    public void testCreateOrder() {
        Order newOrder = Order.builder()
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(200.0)
                .build();

        ResponseEntity<Order> response = restTemplate.postForEntity("/api/order", newOrder, Order.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

//    @Test
//    public void testGetOrderById() {
//        ResponseEntity<Order> response = restTemplate.getForEntity("/api/orders/" + order.getId(), Order.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getId()).isEqualTo(order.getId());
//    }

    @Test
    public void testGetOrderById_NotFound() {
        ResponseEntity<Order> response = restTemplate.getForEntity("/api/order/" + UUID.randomUUID(), Order.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateOrder() {
        order.setTotalAmount(150.0);
        ResponseEntity<Order> response = restTemplate.exchange(
                "/api/order/" + order.getId(),
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(order),
                Order.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalAmount()).isEqualTo(150.0);
    }

    @Test
    public void testDeleteOrder() {
        restTemplate.delete("/api/order/" + order.getId());
        ResponseEntity<Order> response = restTemplate.getForEntity("/api/order/" + order.getId(), Order.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}