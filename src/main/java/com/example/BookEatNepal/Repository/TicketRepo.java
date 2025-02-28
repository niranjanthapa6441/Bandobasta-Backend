package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Event;
import com.example.BookEatNepal.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<Ticket,Integer> {
    List<Ticket> findByEvent(Event event);
}
