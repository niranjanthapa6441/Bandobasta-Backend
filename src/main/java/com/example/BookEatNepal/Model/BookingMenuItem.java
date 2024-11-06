package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="booking_menu_item")
public class BookingMenuItem {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @Column(name="name",length=100,nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "booking_id",nullable = false)
    private HallBooking booking;
}
