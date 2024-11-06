package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.FoodSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodSubCategoryRepo extends JpaRepository<FoodSubCategory, Integer> {
    Optional<FoodSubCategory> findByName(String name);
}
