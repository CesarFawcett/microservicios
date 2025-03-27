package edu.unimag.payment.repository;

import edu.unimag.payment.model.Payment;
import edu.unimag.payment.model.PaymentMethod;
import edu.unimag.payment.model.PaymentStatus;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Desactiva la base de datos en memoria
@Testcontainers
class PaymentRepositoryIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    void testSavePayment() {
        Payment payment = Payment.builder()
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        assertNotNull(savedPayment.getId());
        assertEquals(payment.getOrderId(), savedPayment.getOrderId());
        assertEquals(payment.getAmount(), savedPayment.getAmount());
    }

    @Test
    void testFindById() {
        Payment payment = Payment.builder()
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        Optional<Payment> foundPayment = paymentRepository.findById(payment.getId());

        assertTrue(foundPayment.isPresent());
        assertEquals(payment.getId(), foundPayment.get().getId());
    }

    @Test
    void testFindByOrderId() {
        UUID orderId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        Optional<Payment> foundPayment = paymentRepository.findByOrderId(orderId);

        assertTrue(foundPayment.isPresent());
        assertEquals(orderId, foundPayment.get().getOrderId());
    }

    @Test
    void testDeletePayment() {
        Payment payment = Payment.builder()
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        paymentRepository.deleteById(payment.getId());

        Optional<Payment> deletedPayment = paymentRepository.findById(payment.getId());
        assertFalse(deletedPayment.isPresent());
    }
}