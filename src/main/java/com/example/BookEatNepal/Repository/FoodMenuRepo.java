package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.FoodMenu;
import com.example.BookEatNepal.Model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodMenuRepo extends JpaRepository<FoodMenu, Integer> {
    List<FoodMenu> findByMenu(Menu menu);
}
