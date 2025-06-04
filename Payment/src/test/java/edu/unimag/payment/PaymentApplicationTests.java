package edu.unimag.payment;

import edu.unimag.payment.model.Payment;
import edu.unimag.payment.model.PaymentMethod;
import edu.unimag.payment.model.PaymentStatus;
import edu.unimag.payment.repository.PaymentRepository;
import edu.unimag.payment.service.impl.PaymentServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita las anotaciones de Mockito para JUnit 5
class PaymentServiceImplTest {

    @Mock // Crea un mock del PaymentRepository
    private PaymentRepository paymentRepository;

    @InjectMocks // Inyecta los mocks (en este caso, paymentRepository) en PaymentServiceImpl
    private PaymentServiceImpl paymentService;

    private Payment payment1;
    private Payment payment2;
    private UUID payment1Id;
    private UUID payment2Id;
    private UUID orderId1;
    private UUID orderId2;

    @BeforeEach
    void setUp() {
        // Inicializa datos de prueba antes de cada test
        payment1Id = UUID.randomUUID();
        orderId1 = UUID.randomUUID();
        payment1 = Payment.builder()
                .id(payment1Id)
                .orderId(orderId1)
                .amount(100.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        payment2Id = UUID.randomUUID();
        orderId2 = UUID.randomUUID();
        payment2 = Payment.builder()
                .id(payment2Id)
                .orderId(orderId2)
                .amount(250.50)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .status(PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Test for createPayment - Should save and return the payment")
    void createPayment_ShouldSaveAndReturnPayment() {
        // Dado (Given)
        // Cuando (When) - Definimos el comportamiento del mock
        when(paymentRepository.save(payment1)).thenReturn(payment1);

        // Entonces (Then) - Llamamos al método a probar y verificamos
        Payment createdPayment = paymentService.createPayment(payment1);

        assertNotNull(createdPayment);
        assertEquals(payment1.getId(), createdPayment.getId());
        assertEquals(payment1.getAmount(), createdPayment.getAmount());
        // Verificamos que el método save del repository fue llamado exactamente una vez con el objeto payment1
        verify(paymentRepository, times(1)).save(payment1);
    }

    @Test
    @DisplayName("Test for getPaymentById - Should return payment if found")
    void getPaymentById_ShouldReturnPayment_WhenFound() {
        // Dado
        when(paymentRepository.findById(payment1Id)).thenReturn(Optional.of(payment1));

        // Cuando
        Optional<Payment> foundPayment = paymentService.getPaymentById(payment1Id);

        // Entonces
        assertTrue(foundPayment.isPresent());
        assertEquals(payment1.getId(), foundPayment.get().getId());
        verify(paymentRepository, times(1)).findById(payment1Id);
    }

    @Test
    @DisplayName("Test for getPaymentById - Should return empty optional if not found")
    void getPaymentById_ShouldReturnEmptyOptional_WhenNotFound() {
        // Dado
        when(paymentRepository.findById(payment1Id)).thenReturn(Optional.empty());

        // Cuando
        Optional<Payment> foundPayment = paymentService.getPaymentById(payment1Id);

        // Entonces
        assertFalse(foundPayment.isPresent());
        verify(paymentRepository, times(1)).findById(payment1Id);
    }

    @Test
    @DisplayName("Test for getPaymentByOrderId - Should return payment if found by orderId")
    void getPaymentByOrderId_ShouldReturnPayment_WhenFound() {
        // Dado
        when(paymentRepository.findByOrderId(orderId1)).thenReturn(Optional.of(payment1));

        // Cuando
        Optional<Payment> foundPayment = paymentService.getPaymentByOrderId(orderId1.toString());

        // Entonces
        assertTrue(foundPayment.isPresent());
        assertEquals(payment1.getOrderId(), foundPayment.get().getOrderId());
        verify(paymentRepository, times(1)).findByOrderId(orderId1);
    }

    @Test
    @DisplayName("Test for getPaymentByOrderId - Should return empty optional if not found by orderId")
    void getPaymentByOrderId_ShouldReturnEmptyOptional_WhenNotFound() {
        // Dado
        when(paymentRepository.findByOrderId(orderId1)).thenReturn(Optional.empty());

        // Cuando
        Optional<Payment> foundPayment = paymentService.getPaymentByOrderId(orderId1.toString());

        // Entonces
        assertFalse(foundPayment.isPresent());
        verify(paymentRepository, times(1)).findByOrderId(orderId1);
    }

    @Test
    @DisplayName("Test for getAllPayments - Should return a list of payments")
    void getAllPayments_ShouldReturnListOfPayments() {
        // Dado
        List<Payment> payments = Arrays.asList(payment1, payment2);
        when(paymentRepository.findAll()).thenReturn(payments);

        // Cuando
        List<Payment> retrievedPayments = paymentService.getAllPayments();

        // Entonces
        assertNotNull(retrievedPayments);
        assertEquals(2, retrievedPayments.size());
        assertTrue(retrievedPayments.contains(payment1));
        assertTrue(retrievedPayments.contains(payment2));
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test for updatePayment - Should update and return the payment if exists")
    void updatePayment_ShouldUpdateAndReturnPayment_WhenExists() {
        // Dado
        Payment updatedDetails = Payment.builder()
                .amount(120.0)
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(PaymentMethod.DEBIT_CARD)
                .paymentDate(LocalDateTime.now())
                .build();

        // Cuando
        when(paymentRepository.existsById(payment1Id)).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedDetails); // Captura el objeto Payment guardado

        Payment result = paymentService.updatePayment(payment1Id, updatedDetails);

        // Entonces
        assertNotNull(result);
        assertEquals(payment1Id, result.getId()); // Verifica que el ID se mantiene
        assertEquals(120.0, result.getAmount());
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentRepository, times(1)).existsById(payment1Id);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Test for updatePayment - Should throw RuntimeException if payment not found")
    void updatePayment_ShouldThrowRuntimeException_WhenNotFound() {
        // Dado
        Payment updatedDetails = Payment.builder().amount(120.0).build();
        when(paymentRepository.existsById(payment1Id)).thenReturn(false);

        // Cuando/Entonces
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.updatePayment(payment1Id, updatedDetails);
        });

        assertEquals("Payment not found with id: " + payment1Id, exception.getMessage());
        verify(paymentRepository, times(1)).existsById(payment1Id);
        verify(paymentRepository, never()).save(any(Payment.class)); // Asegura que save nunca fue llamado
    }

}