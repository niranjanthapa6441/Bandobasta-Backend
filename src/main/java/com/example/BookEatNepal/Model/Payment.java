package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name="payment")
public class Payment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name="id",length=10)
    private int id;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private HallBooking booking;

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
