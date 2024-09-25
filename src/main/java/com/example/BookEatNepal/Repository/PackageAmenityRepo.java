package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.PackageAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageAmenityRepo extends JpaRepository<PackageAmenity, Integer> {
}
