package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.BookingMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingMenuItemRepo extends JpaRepository<BookingMenuItem, Integer> {

}
