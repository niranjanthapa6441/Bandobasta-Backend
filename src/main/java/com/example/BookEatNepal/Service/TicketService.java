package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Model.Ticket;
import com.example.BookEatNepal.Payload.Request.TicketRequest;
import com.example.BookEatNepal.Payload.Request.UpdateTicketRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TicketService {
    String save(TicketRequest request);
    String delete(int id);
    String update(int id, UpdateTicketRequest request);
    List<Ticket> findAll(int eventId);
}
