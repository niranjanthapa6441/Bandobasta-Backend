package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.EventType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name="hall_booking")
public class HallBooking {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private HallAvailability hallAvailability;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "requested_date",nullable = false)
    private LocalDate requestedDate;

    @Column(nullable = true, name = "confirmed_date")
    private LocalDate confirmedDate;

    @Column(nullable = true, name = "booked_for_date")
    private LocalDate bookedForDate;

    @Column(nullable = false,name = "requested_time")
    private LocalTime requestedTime;

    @Column(nullable = true,name = "confirmed_time")
    private LocalTime confirmedTime;

    @Column(name = "price", nullable = true)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type",length = 30, nullable = false)
    private EventType eventType;
}
