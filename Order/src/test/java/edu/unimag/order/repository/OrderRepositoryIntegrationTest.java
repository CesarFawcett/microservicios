package edu.unimag.order.repository;

import edu.unimag.order.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OrderRepositoryIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll(); // Limpia la base de datos antes de cada prueba
    }

    @Test
    void testSaveOrder() {
        // Arrange
        Order order = Order.builder()
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();

        // Act
        Order savedOrder = orderRepository.save(order);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals(order.getCustomerId(), savedOrder.getCustomerId());
        assertEquals(order.getTotalAmount(), savedOrder.getTotalAmount());
    }

    @Test
    void testFindById() {
        // Arrange
        Order order = Order.builder()
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();
        Order savedOrder = orderRepository.save(order);

        // Act
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(savedOrder.getId(), foundOrder.get().getId());
    }

    @Test
    void testDeleteOrder() {
        // Arrange
        Order order = Order.builder()
                .customerId(UUID.randomUUID())
                .orderDate(LocalDateTime.now())
                .totalAmount(100.0)
                .build();
        Order savedOrder = orderRepository.save(order);

        // Act
        orderRepository.deleteById(savedOrder.getId());

        // Assert
        Optional<Order> deletedOrder = orderRepository.findById(savedOrder.getId());
        assertFalse(deletedOrder.isPresent());
    }
}