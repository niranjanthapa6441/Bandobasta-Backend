package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

@Data
public class PaymentRequest {
    private String paymentMethod;
    private String paymentPartner;
    private double amount;
}
