package com.example.BookEatNepal.Service;


import com.example.BookEatNepal.DTO.BookingDTO;
import com.example.BookEatNepal.DTO.BookingDetail;
import com.example.BookEatNepal.Request.BookingRequest;
import org.springframework.stereotype.Service;

@Service
public interface HallBookingService {
    String save(BookingRequest request);
    String delete(int id);

    BookingDTO findBookingByUser(String userId, String bookingDate, int page, int size);
    BookingDTO findBookingByVenue(String venueId, String bookingDate, String hallId, int page, int size);

    BookingDetail findById(int id);

    String update(BookingRequest request, int id );

    String confirmBooking(int id );
}
