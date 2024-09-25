package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.PackageAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageAvailabilityRepo extends JpaRepository<PackageAvailability, Integer> {
}
