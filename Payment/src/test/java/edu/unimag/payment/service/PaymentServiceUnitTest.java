package edu.unimag.payment.service;

import edu.unimag.payment.model.Payment;
import edu.unimag.payment.model.PaymentMethod;
import edu.unimag.payment.model.PaymentStatus;
import edu.unimag.payment.repository.PaymentRepository;
import edu.unimag.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService; // Asume que tienes una implementación llamada PaymentServiceImpl

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment createdPayment = paymentService.createPayment(payment);

        assertNotNull(createdPayment);
        assertEquals(payment.getOrderId(), createdPayment.getOrderId());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testGetPaymentById() {
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

        Optional<Payment> foundPayment = paymentService.getPaymentById(paymentId);

        assertTrue(foundPayment.isPresent());
        assertEquals(paymentId, foundPayment.get().getId());
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPaymentByIdNotFound() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        Optional<Payment> foundPayment = paymentService.getPaymentById(paymentId);

        assertFalse(foundPayment.isPresent());
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPaymentByOrderId() {
        UUID orderId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        Optional<Payment> foundPayment = paymentService.getPaymentByOrderId(orderId.toString());

        assertTrue(foundPayment.isPresent());
        assertEquals(orderId, foundPayment.get().getOrderId());
        verify(paymentRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void testGetPaymentByOrderIdNotFound() {
        UUID orderId = UUID.randomUUID();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        Optional<Payment> foundPayment = paymentService.getPaymentByOrderId(orderId.toString());

        assertFalse(foundPayment.isPresent());
        verify(paymentRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void testUpdatePayment() {
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

        // Simula que el pago existe en el repositorio
        when(paymentRepository.existsById(paymentId)).thenReturn(true);
        // Simula que el repositorio devuelve el pago actualizado
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        Payment updatedPayment = paymentService.updatePayment(paymentId, payment);

        // Assert
        assertNotNull(updatedPayment);
        assertEquals(paymentId, updatedPayment.getId());
        verify(paymentRepository, times(1)).existsById(paymentId);
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testUpdatePaymentNotFound() {
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

        // Simula que el pago no existe en el repositorio
        when(paymentRepository.existsById(paymentId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.updatePayment(paymentId, payment);
        });

        // Verifica el mensaje de la excepción
        assertEquals("Payment not found with id: " + paymentId, exception.getMessage());

        // Verifica que se llamó a existsById y no a findById
        verify(paymentRepository, times(1)).existsById(paymentId);
        verify(paymentRepository, never()).findById(any(UUID.class)); // Nunca se llama a findById
        verify(paymentRepository, never()).save(any(Payment.class)); // Nunca se llama a save
    }

    @Test
    void testDeletePayment() {
        UUID paymentId = UUID.randomUUID();
        doNothing().when(paymentRepository).deleteById(paymentId);

        paymentService.deletePayment(paymentId);

        verify(paymentRepository, times(1)).deleteById(paymentId);
    }
}