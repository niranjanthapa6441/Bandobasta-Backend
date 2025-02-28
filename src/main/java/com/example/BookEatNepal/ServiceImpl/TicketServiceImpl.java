package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Model.Event;
import com.example.BookEatNepal.Model.Ticket;
import com.example.BookEatNepal.Payload.Request.TicketRequest;
import com.example.BookEatNepal.Payload.Request.UpdateTicketRequest;
import com.example.BookEatNepal.Repository.EventRepo;
import com.example.BookEatNepal.Repository.TicketRepo;
import com.example.BookEatNepal.Service.TicketService;
import com.example.BookEatNepal.Util.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    TicketRepo ticketRepo;

    @Autowired
    EventRepo eventRepo;

    @Override
    public String save(TicketRequest request) {
        return "";
    }

    @Override
    public String delete(int id) {
        return "";
    }

    @Override
    public String update(int id, UpdateTicketRequest request) {
        return "";
    }

    @Override
    public List<Ticket> findAll(int eventId) {
        Event event = getEvent(eventId);
        return ticketRepo.findByEvent(event);
    }

    private Event getEvent(int eventId) {
        return eventRepo.findById(eventId).orElseThrow(()->new CustomException(CustomException.Type.EVENT_NOT_FOUND));
    }
}
