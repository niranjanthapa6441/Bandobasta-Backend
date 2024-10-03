package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.EventType;
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

    @Column(name = "name", columnDefinition = "TEXT", nullable = false)
    private String name ;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Enumerated(EnumType.STRING)
    @Column(name="package_type",length = 20, nullable = false)
    private PackageType packageType;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type",length = 30, nullable = false)
    private EventType eventType;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;


    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private PackageStatus status;
}
