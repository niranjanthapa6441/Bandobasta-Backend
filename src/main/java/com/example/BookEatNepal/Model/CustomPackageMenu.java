package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.CustomPackageStatus;
import com.example.BookEatNepal.Enums.FoodCategory;
import com.example.BookEatNepal.Enums.MenuStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="custom_package_menu")
public class CustomPackageMenu {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_package_id", nullable = false)
    private CustomPackage customPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Enumerated(EnumType.STRING)
    @Column(name="food_category",length = 20, nullable = false)
    private FoodCategory foodCategory;
}
