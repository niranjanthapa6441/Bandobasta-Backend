package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name="event_order")
public class Order {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private EventUser eventUser;

    @Column(name = "order_date",nullable = false)
    private LocalDate orderDate;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name="number_of_tickets",nullable = false)
    private int numberOfTickets;

    @Column(name = "order_status",nullable = false)
    private OrderStatus orderStatus;
}
