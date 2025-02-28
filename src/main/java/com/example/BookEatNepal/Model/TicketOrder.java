package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="ticket_order")
public class TicketOrder {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "ticket_id",nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @Column(name = "order_status",nullable = false)
    private OrderStatus orderStatus;
}
