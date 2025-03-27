package edu.unimag.payment.repository;

import edu.unimag.payment.model.Payment;
import edu.unimag.payment.model.PaymentMethod;
import edu.unimag.payment.model.PaymentStatus;
import edu.unimag.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentRepositoryUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService; // Usa la implementación concreta

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        // Arrange
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        Optional<Payment> foundPayment = paymentService.getPaymentById(paymentId);

        // Assert
        assertEquals(payment, foundPayment.get());
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testSavePayment() {
        // Arrange
        Payment payment = Payment.builder()
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        Payment savedPayment = paymentService.createPayment(payment);

        // Assert
        assertEquals(payment, savedPayment);
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testDeletePayment() {
        // Arrange
        UUID paymentId = UUID.randomUUID();
        doNothing().when(paymentRepository).deleteById(paymentId);

        // Act
        paymentService.deletePayment(paymentId);

        // Assert
        verify(paymentRepository, times(1)).deleteById(paymentId);
    }
}