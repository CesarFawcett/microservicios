package edu.unimag.payment.model;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("Pago con tarjeta de crédito"),
    DEBIT_CARD("Pago con tarjeta de débito"),
    BANK_TRANSFER("Transferencia bancaria"),
    CASH("Pago en efectivo");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

}