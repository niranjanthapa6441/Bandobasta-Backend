package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.CustomPackageBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomPackageBookingRepo extends JpaRepository<CustomPackageBooking,Integer> {
}
