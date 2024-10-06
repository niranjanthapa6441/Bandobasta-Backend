package com.example.BookEatNepal.Service;


import com.example.BookEatNepal.DTO.HallBookingDTO;
import com.example.BookEatNepal.DTO.HallBookingDetail;
import com.example.BookEatNepal.Request.HallBookingRequest;
import org.springframework.stereotype.Service;

@Service
public interface HallBookingService {
    String save(HallBookingRequest request);
    String delete(int id);

    HallBookingDTO findBookingByUser(String userId, String bookingDate, int page, int size);
    HallBookingDTO findBookingByVenue(String venueId, String bookingDate, String hallId, int page, int size);

    HallBookingDetail findById(int id);

    String update(HallBookingRequest request, int id );

    String confirmBooking(int id );
}
