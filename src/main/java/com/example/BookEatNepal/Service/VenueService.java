package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.DTO.VenueDTO;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Request.VenueRequest;
import org.springframework.stereotype.Service;

@Service
public interface VenueService {
    String save(VenueRequest venueRequest);

    String delete(int id);


    VenueDTO findAll(String venue, int page, int size);

    Venue findById(int id);

    String update(VenueRequest request, int id);
}
