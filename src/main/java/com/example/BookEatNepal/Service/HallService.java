package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.DTO.HallAvailabilityDTO;
import com.example.BookEatNepal.DTO.HallDTO;
import com.example.BookEatNepal.DTO.HallDetail;
import com.example.BookEatNepal.Request.HallAvailabilityRequest;
import com.example.BookEatNepal.Request.HallRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public interface HallService {
    String save(HallRequest request, List<MultipartFile> hallImages);

    String delete(int id);


    HallDTO findAll(String venueId, int page, int size);

    HallDetail findById(int id);

    String update(HallRequest request, int id, List<MultipartFile> hallImages);

    String saveHallAvailability(List<HallAvailabilityRequest> requests);

    HallAvailabilityDTO checkAvailability(String venueId, LocalDate date, String startTime, String endTime, int numberOfGuests, int page, int size);
}
