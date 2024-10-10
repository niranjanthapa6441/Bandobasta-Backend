package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepo extends JpaRepository<Venue, Integer>, JpaSpecificationExecutor<Venue> {
}
