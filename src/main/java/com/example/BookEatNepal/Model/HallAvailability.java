package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.HallStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name="hall_availability")
public class HallAvailability {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false, name = "date")
    private LocalDate date;

    @Column(nullable = false,name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false,name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private HallStatus status;
}
