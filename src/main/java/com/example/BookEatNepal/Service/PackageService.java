package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Payload.DTO.PackageAvailabilityDTO;
import com.example.BookEatNepal.Payload.DTO.PackageDTO;
import com.example.BookEatNepal.Payload.DTO.PackageDetail;
import com.example.BookEatNepal.Payload.Request.HallAvailabilityRequest;
import com.example.BookEatNepal.Payload.Request.PackageAvailabilityRequest;
import com.example.BookEatNepal.Payload.Request.PackageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface PackageService {
    String save(PackageRequest request);

    String delete(int id);

    PackageDTO findPackageByVenue(String venueId, String packageType, String eventType, int page, int size);

    PackageDetail findById(int id);

    String update(PackageRequest request, int id);
    String savePackageAvailability(List<PackageAvailabilityRequest> requests);

    PackageAvailabilityDTO checkAvailability(String venueId, String date, String startTime, String endTime, int numberOfGuests, int page, int size);

}
