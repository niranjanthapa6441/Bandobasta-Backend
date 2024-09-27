package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.PackageStatus;
import com.example.BookEatNepal.Enums.PackageType;
import com.example.BookEatNepal.Enums.VenueStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="package")
public class Package {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name="id",length=10)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Enumerated(EnumType.STRING)
    @Column(name="package_type",length = 20, nullable = false)
    private PackageType packageType;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private PackageStatus status;
}
