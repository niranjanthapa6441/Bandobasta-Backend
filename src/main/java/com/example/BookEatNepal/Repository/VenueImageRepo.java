package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.VenueImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueImageRepo extends JpaRepository<VenueImage, Integer> {
}
