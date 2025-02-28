package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.FoodCategory;
import com.example.BookEatNepal.Model.FoodSubCategory;
import com.example.BookEatNepal.Model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodSubCategoryRepo extends JpaRepository<FoodSubCategory, Integer> {
    Optional<FoodSubCategory> findByName(String name);

    @Query("SELECT f FROM FoodSubCategory f WHERE f.name = :name AND f.foodCategory.venue = :venue AND f.foodCategory = :category")
    Optional<FoodSubCategory> findByNameAndVenueAndCategory(@Param("name") String name, @Param("venue") Venue venue, @Param("category") FoodCategory category);
}
