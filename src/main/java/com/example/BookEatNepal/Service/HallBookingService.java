package com.example.BookEatNepal.Service;


import com.example.BookEatNepal.Payload.DTO.HallBookingDTO;
import com.example.BookEatNepal.Payload.DTO.HallBookingDetail;
import com.example.BookEatNepal.Payload.Request.BookingRequest;
import com.example.BookEatNepal.Payload.Request.UpdateBookingRequest;
import org.springframework.stereotype.Service;

@Service
public interface HallBookingService {
    String save(BookingRequest request);
    String delete(int id);

    HallBookingDTO findBookingByUser(String userId, String startDate,String endDate,String bookingStatus,String orderBy, int page, int size);
    HallBookingDTO findBookingByVenue(String venueId, String bookingDate, String hallId, int page, int size);

    HallBookingDetail findById(int id);

    String update(UpdateBookingRequest request, int id );

    String confirmBooking(int id );
    String cancelBooking(int id );

}
