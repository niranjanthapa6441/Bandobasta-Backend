package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Integer> {
}
