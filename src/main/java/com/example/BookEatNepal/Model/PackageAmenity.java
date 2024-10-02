package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="package_amenity")
public class PackageAmenity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY )
    @Column(name="id",length=10)
    private int id;
    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Package aPackage;

    @ManyToOne
    @JoinColumn(name = "amenity_id", nullable = false)
    private Amenity amenity;

}
