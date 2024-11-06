package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="menu_item_selection_range")
public class MenuItemSelectionRange {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne
    @JoinColumn(name="sub_category_id", nullable = false)
    private FoodSubCategory foodSubCategory;

    @ManyToOne
    @JoinColumn(name="menu_id", nullable = false)
    private Menu menu;

    @Column(name = "max_selection", nullable = false)
    private int maxSelection;
}
