package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.FoodCategory;
import com.example.BookEatNepal.Enums.FoodStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="food_menu")
public class FoodMenu {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;
}
