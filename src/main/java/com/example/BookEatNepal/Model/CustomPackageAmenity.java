package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="custom_package_amenity")
public class CustomPackageAmenity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "custom_package_id", nullable = false)
    private CustomPackage customPackage;

    @ManyToOne
    @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity;
}
