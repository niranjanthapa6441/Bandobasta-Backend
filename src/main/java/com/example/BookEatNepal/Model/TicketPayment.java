package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name="ticket_payment")
public class TicketPayment {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name="id",length=10)
    private int id;

    @OneToOne
    @JoinColumn(name = "ticket_order_id")
    private Order order;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_time")
    private LocalTime paymentTime;

    @Column(name = "payment_partner")
    private String paymentPartner;

    @Column(name = "payment_method",nullable = false)
    private String paymentMethod;

    @Column(name = "paid_amount",nullable = false)
    private double paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private PaymentStatus status;
}
