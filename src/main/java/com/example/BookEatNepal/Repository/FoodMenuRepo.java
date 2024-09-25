package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.FoodMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodMenuRepo extends JpaRepository<FoodMenu, Integer> {
}
