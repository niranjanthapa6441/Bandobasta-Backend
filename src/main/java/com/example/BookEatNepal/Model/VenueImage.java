package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "venue_image")
public class VenueImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10)
    private int id;

    @Column(name = "image_url", nullable = false, unique = true)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
}

