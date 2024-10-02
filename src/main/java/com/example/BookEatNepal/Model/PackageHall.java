package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="package_hall")
public class PackageHall {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY)
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Package aPackage;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;
}
