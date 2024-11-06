package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.FoodCategory;
import com.example.BookEatNepal.Enums.FoodStatus;
import com.example.BookEatNepal.Enums.PackageType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="food")
public class Food {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name="item", nullable = false, length = 30)
    private String name;


    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private FoodStatus status;

    @ManyToOne
    @JoinColumn(name = "sub_category_id", nullable = false)
    private FoodSubCategory subCategory;
}
