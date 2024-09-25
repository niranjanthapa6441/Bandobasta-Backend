package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.HallStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name="booking")
public class Booking {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_package_id", nullable = true)
    private CustomPackage customPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = true)
    private Package aPackage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = true)
    private Hall hall;

    @Column(nullable = false, name = "date")
    private LocalDate date;

    @Column(nullable = false,name = "time")
    private LocalTime time;
    @Column(name = "price", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private BookingStatus status;
}
