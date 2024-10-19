package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Hall;
import com.example.BookEatNepal.Model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallRepo extends JpaRepository<Hall, Integer> {

    List<Hall> findByVenueId(int venueId);

    List<Hall> findByVenue(Venue venue);
}
