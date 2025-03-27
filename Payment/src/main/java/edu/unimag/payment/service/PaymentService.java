package edu.unimag.payment.service;

import edu.unimag.payment.model.Payment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    Payment createPayment(Payment payment);
    Optional<Payment> getPaymentById(UUID id);
    Optional<Payment> getPaymentByOrderId(String orderId);
    List<Payment> getAllPayments();
    Payment updatePayment(UUID id, Payment payment);
    void deletePayment(UUID id);
}
