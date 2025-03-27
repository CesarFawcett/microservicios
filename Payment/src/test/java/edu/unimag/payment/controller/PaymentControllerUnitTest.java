package edu.unimag.payment.controller;

import edu.unimag.payment.model.Payment;
import edu.unimag.payment.model.PaymentMethod;
import edu.unimag.payment.model.PaymentStatus;
import edu.unimag.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerUnitTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private Payment payment;

    @BeforeEach
    public void setUp() {
        payment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCreatePayment() {
        when(paymentService.createPayment(any(Payment.class))).thenReturn(payment);

        ResponseEntity<Payment> response = paymentController.createPayment(payment);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(payment);
        verify(paymentService, times(1)).createPayment(payment);
    }

    @Test
    public void testGetPaymentById() {
        when(paymentService.getPaymentById(payment.getId())).thenReturn(Optional.of(payment));

        ResponseEntity<Payment> response = paymentController.getPaymentById(payment.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(payment);
        verify(paymentService, times(1)).getPaymentById(payment.getId());
    }

    @Test
    public void testGetPaymentById_NotFound() {
        when(paymentService.getPaymentById(any(UUID.class))).thenReturn(Optional.empty());

        ResponseEntity<Payment> response = paymentController.getPaymentById(UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(paymentService, times(1)).getPaymentById(any(UUID.class));
    }

    @Test
    public void testGetPaymentByOrderId() {
        when(paymentService.getPaymentByOrderId(payment.getOrderId().toString())).thenReturn(Optional.of(payment));

        ResponseEntity<Payment> response = paymentController.getPaymentByOrderId(payment.getOrderId().toString());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(payment);
        verify(paymentService, times(1)).getPaymentByOrderId(payment.getOrderId().toString());
    }

    @Test
    public void testUpdatePayment() {
        when(paymentService.updatePayment(payment.getId(), payment)).thenReturn(payment);

        ResponseEntity<Payment> response = paymentController.updatePayment(payment.getId(), payment);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(payment);
        verify(paymentService, times(1)).updatePayment(payment.getId(), payment);
    }

    @Test
    public void testDeletePayment() {
        doNothing().when(paymentService).deletePayment(payment.getId());

        ResponseEntity<Void> response = paymentController.deletePayment(payment.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(paymentService, times(1)).deletePayment(payment.getId());
    }
}