package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.BookingMenuItem;
import com.example.BookEatNepal.Model.Food;
import com.example.BookEatNepal.Model.HallBooking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingMenuItemRepo extends JpaRepository<BookingMenuItem, Integer> {

    BookingMenuItem findByBooking(HallBooking hallBooking);

    @Query("SELECT bmi.food FROM BookingMenuItem bmi WHERE bmi.booking = :hallBooking")
    List<Food> findFoodsByBookingId(HallBooking hallBooking);

    @Transactional
    @Modifying
    @Query("DELETE FROM BookingMenuItem bmi WHERE bmi.booking = :booking")
    void deleteAllByBooking(@Param("booking") HallBooking booking);

}
