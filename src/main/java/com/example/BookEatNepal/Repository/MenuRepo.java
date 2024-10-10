package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Menu;
import com.example.BookEatNepal.Model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Integer> {
    List<Menu> findByVenue(Venue venue);
}
