package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.VenueStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="venue")
public class Venue {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @Column(name="venue_name", length = 50, nullable = false)
    private String venueName;

    @Column(name="email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name="primary_phone_number", length = 10, nullable=false, unique = true)
    private String primaryPhoneNumber;

    @Column(name="secondary_phone_number", length = 10, nullable=true, unique = true)
    private String secondaryPhoneNumber;

    @Column(name="country_code", length = 3, nullable=false)
    private String countryCode;

    @Column(name = "registration_number", nullable = false, length = 15, unique = true)
    private String registrationNumber;

    @Column(name = "license_number", nullable = false, length = 15, unique = true)
    private String licenseNumber;

    @Column(name = "license_image_url", nullable = false, unique = true)
    private String licenseImagePath;

    @Column(name="permanent_account_number", nullable = false, unique = true)
    private String permanentAccountNumber;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "pan_image_url", nullable = false)
    private String panImagePath;

    @Column(name = "address", nullable = false)
    private String address;
    @Lob
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private VenueStatus status;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VenueImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Package> packages = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hall> halls = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Amenity> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Food> foods = new ArrayList<>();
}

