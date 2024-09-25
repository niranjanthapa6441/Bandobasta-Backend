package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepo extends JpaRepository<Amenity, Integer> {

}
