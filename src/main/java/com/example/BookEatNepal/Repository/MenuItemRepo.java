package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.MenuItem;
import com.example.BookEatNepal.Model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByMenu(Menu menu);
}
