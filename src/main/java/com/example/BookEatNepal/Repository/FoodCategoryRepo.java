package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodCategoryRepo extends JpaRepository<FoodCategory,Integer> {
    Optional<FoodCategory> findByName(String category);
}
