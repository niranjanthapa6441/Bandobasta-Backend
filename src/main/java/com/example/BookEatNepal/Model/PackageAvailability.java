package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Enums.PackageStatus;
import com.example.BookEatNepal.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name="package_availability")
public class PackageAvailability {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Package aPackage;
    @Column(nullable = false, name = "date")
    private LocalDate date;

    @Column(nullable = false,name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false,name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private PackageStatus status;
}
