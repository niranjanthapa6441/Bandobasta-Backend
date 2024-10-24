package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.CustomPackageStatus;
import com.example.BookEatNepal.Enums.EventType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name="Custom_Package_Booking")
public class CustomPackageBooking {
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
    @JoinColumn(name = "custom_package_id", nullable = false)
    private CustomPackage customPackage;

    @Column(name = "requested_date",nullable = false)
    private LocalDate requestedDate;

    @Column(nullable = false, name = "confirmed_date")
    private LocalDate confirmedDate;

    @Column(nullable = false,name = "requested_time")
    private LocalTime requestedTime;

    @Column(nullable = false,name = "confirmed_time")
    private LocalTime confirmedTime;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 30, nullable = false)
    private CustomPackageStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type",length = 30, nullable = false)
    private EventType eventType;
}
