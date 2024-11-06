package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Menu;
import com.example.BookEatNepal.Model.MenuItemSelectionRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemSelectionRangeRepo extends JpaRepository<MenuItemSelectionRange, Integer> {

    List<MenuItemSelectionRange> findByMenu(Menu menu);
}
