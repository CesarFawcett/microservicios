package edu.unimag.payment.service;

import edu.unimag.payment.model.Payment;
import edu.unimag.payment.model.PaymentMethod;
import edu.unimag.payment.model.PaymentStatus;
import edu.unimag.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class PaymentServiceIntegrationTest {

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
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    void testCreatePayment() {
        Payment payment = Payment.builder()
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        Payment createdPayment = paymentService.createPayment(payment);

        assertNotNull(createdPayment.getId());
        assertEquals(payment.getOrderId(), createdPayment.getOrderId());
    }

    @Test
    void testGetPaymentById() {
        Payment payment = Payment.builder()
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        Optional<Payment> foundPayment = paymentService.getPaymentById(payment.getId());

        assertTrue(foundPayment.isPresent());
        assertEquals(payment.getId(), foundPayment.get().getId());
    }

    @Test
    void testGetPaymentByOrderId() {
        UUID orderId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        Optional<Payment> foundPayment = paymentService.getPaymentByOrderId(orderId.toString());

        assertTrue(foundPayment.isPresent());
        assertEquals(orderId, foundPayment.get().getOrderId());
    }

    @Test
    void testUpdatePayment() {
        Payment payment = Payment.builder()
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        payment.setAmount(200.0);
        payment.setStatus(PaymentStatus.COMPLETED);

        Payment updatedPayment = paymentService.updatePayment(payment.getId(), payment);

        assertNotNull(updatedPayment);
        assertEquals(200.0, updatedPayment.getAmount());
        assertEquals(PaymentStatus.COMPLETED, updatedPayment.getStatus());
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

        paymentService.deletePayment(payment.getId());

        Optional<Payment> deletedPayment = paymentRepository.findById(payment.getId());
        assertFalse(deletedPayment.isPresent());
    }
}