package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Payload.DTO.HallAvailabilityDTO;
import com.example.BookEatNepal.Payload.DTO.HallDTO;
import com.example.BookEatNepal.Payload.DTO.HallDetail;
import com.example.BookEatNepal.Payload.Request.HallAvailabilityRequest;
import com.example.BookEatNepal.Payload.Request.HallRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public interface HallService {

    String save(HallRequest request, List<MultipartFile> hallImages);

    String delete(int id);

    HallDTO findAll(String venueId, int numberOfGuests, int page, int size, String checkAvailableDate);

    HallDetail findById(int id);

    String update(HallRequest request, int id, List<MultipartFile> hallImages);

    String saveHallAvailability(List<HallAvailabilityRequest> requests);

    HallAvailabilityDTO checkAvailability(String venueId, int hallId, String date, int numberOfGuests, int page, int size);

    String updateHallAvailability(String shift, String status, LocalDate date);
}