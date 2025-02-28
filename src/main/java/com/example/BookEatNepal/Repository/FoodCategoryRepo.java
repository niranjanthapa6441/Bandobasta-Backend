package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.FoodCategory;
import com.example.BookEatNepal.Model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodCategoryRepo extends JpaRepository<FoodCategory,Integer> {
    Optional<FoodCategory> findByNameAndVenue(String name, Venue venue);
}
