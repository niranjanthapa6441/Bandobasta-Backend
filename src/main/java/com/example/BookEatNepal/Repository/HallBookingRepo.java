package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.HallBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallBookingRepo extends JpaRepository<HallBooking,Integer> {
}
