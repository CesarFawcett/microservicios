package edu.unimag.payment.controller;

import edu.unimag.payment.model.Payment;
import edu.unimag.payment.model.PaymentMethod;
import edu.unimag.payment.model.PaymentStatus;
import edu.unimag.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PaymentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    private String baseUrl;

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

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/payment";
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

        ResponseEntity<Payment> response = restTemplate.postForEntity(baseUrl, payment, Payment.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
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

        ResponseEntity<Payment> response = restTemplate.getForEntity(baseUrl + "/" + payment.getId(), Payment.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(payment.getId(), response.getBody().getId());
    }

    @Test
    void testGetPaymentByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<Payment> response = restTemplate.getForEntity(baseUrl + "/" + nonExistentId, Payment.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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

        ResponseEntity<Payment> response = restTemplate.getForEntity(baseUrl + "/order/" + orderId, Payment.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderId, response.getBody().getOrderId());
    }

    @Test
    void testGetPaymentByOrderIdNotFound() {
        UUID nonExistentOrderId = UUID.randomUUID();
        ResponseEntity<Payment> response = restTemplate.getForEntity(baseUrl + "/order/" + nonExistentOrderId, Payment.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
        restTemplate.put(baseUrl + "/" + payment.getId(), payment);

        ResponseEntity<Payment> updatedResponse = restTemplate.getForEntity(baseUrl + "/" + payment.getId(), Payment.class);

        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        assertEquals(200.0, updatedResponse.getBody().getAmount());
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

        restTemplate.delete(baseUrl + "/" + payment.getId());

        ResponseEntity<Payment> response = restTemplate.getForEntity(baseUrl + "/" + payment.getId(), Payment.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}