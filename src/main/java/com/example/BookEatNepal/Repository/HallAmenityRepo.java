package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.HallAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallAmenityRepo extends JpaRepository<HallAmenity, Integer> {
}
