package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Amenity;
import com.example.BookEatNepal.Model.Package;
import com.example.BookEatNepal.Model.PackageAmenity;
import com.example.BookEatNepal.Model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepo extends JpaRepository<Amenity, Integer> {

    List<Amenity> findByVenue(Venue venue);
}
