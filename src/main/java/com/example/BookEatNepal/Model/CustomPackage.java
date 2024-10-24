package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="custom_package")
public class CustomPackage {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @OneToMany(mappedBy = "customPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomPackageAmenity> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "customPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomPackageMenu> foods = new ArrayList<>();
}
