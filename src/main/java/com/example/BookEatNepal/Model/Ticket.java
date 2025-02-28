package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="ticket")
public class Ticket {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name="ticket_type",nullable = false)
    private String ticketType;
}
