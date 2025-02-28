package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Model.Event;
import com.example.BookEatNepal.Payload.Request.EventRequest;
import com.example.BookEatNepal.Payload.Request.UpdateEventRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {
    String save(EventRequest request);
    String delete(int id);
    String update(int id, UpdateEventRequest request);
    List<Event> findAll();

}
