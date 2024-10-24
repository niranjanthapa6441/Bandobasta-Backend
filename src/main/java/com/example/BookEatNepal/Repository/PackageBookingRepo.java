package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.PackageBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageBookingRepo extends JpaRepository<PackageBooking, Integer> {
}
