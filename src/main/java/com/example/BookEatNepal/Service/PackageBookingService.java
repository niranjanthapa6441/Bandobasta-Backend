package com.example.BookEatNepal.Service;


import com.example.BookEatNepal.Payload.DTO.PackageBookingDTO;
import com.example.BookEatNepal.Payload.DTO.PackageBookingDetail;
import com.example.BookEatNepal.Payload.Request.PackageBookingRequest;
import org.springframework.stereotype.Service;

@Service
public interface PackageBookingService {
    String save(PackageBookingRequest request);
    String delete(int id);

    PackageBookingDTO findBookingByUser(String userId, String bookingDate, int page, int size);
    PackageBookingDTO findBookingByVenue(String venueId, String bookingDate, String hallId, int page, int size);

    PackageBookingDetail findById(int id);

    String update(PackageBookingRequest request, int id );

    String confirmBooking(int id );
}
