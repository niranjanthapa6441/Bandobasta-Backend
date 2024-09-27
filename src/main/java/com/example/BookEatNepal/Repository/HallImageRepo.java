package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.HallImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallImageRepo extends JpaRepository<HallImage, Integer> {
    List<HallImage> findByHallId(int hallId);
}
