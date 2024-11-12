package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.HallStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="hall")
public class Hall {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name="name", nullable = false, length = 30)
    private String name;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name="floor_number", nullable = false)
    private int floorNumber;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "price", nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private HallStatus status;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HallImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HallAmenity> amenities = new ArrayList<>();
}
