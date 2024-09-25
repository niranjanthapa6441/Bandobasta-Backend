package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.AmenityStatus;
import com.example.BookEatNepal.Enums.FoodStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="amenity")
public class Amenity {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column(name="id",length=10)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
    @Column(name="item", nullable = false, length = 30)
    private String name;
    @Lob
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    @Column(name = "price", nullable = false)
    private double price;
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private AmenityStatus status;
}
