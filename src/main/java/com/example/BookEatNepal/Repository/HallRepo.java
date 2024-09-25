package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallRepo extends JpaRepository<Hall, Integer> {
}
