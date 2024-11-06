package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="food_category")
public class FoodCategory {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @Column(name="name",length=100,nullable = false,unique = true)
    private String name;
}
