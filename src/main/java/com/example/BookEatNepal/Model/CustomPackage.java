package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.CustomPackageStatus;
import com.example.BookEatNepal.Enums.VenueStatus;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CustomPackageStatus status;

    @OneToMany(mappedBy = "customPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomPackageAmenity> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "customPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomPackageMenu> foods = new ArrayList<>();
}
