package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="hall_amenity")
public class HallAmenity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity;
}
