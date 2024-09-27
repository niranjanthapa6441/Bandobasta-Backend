package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.DTO.HallAvailabilityDTO;
import com.example.BookEatNepal.DTO.HallDTO;
import com.example.BookEatNepal.DTO.HallDetails;
import com.example.BookEatNepal.Model.Hall;
import com.example.BookEatNepal.Model.HallAvailability;
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

    HallDetails findById(int id);

    String update(HallRequest request, int id, List<MultipartFile> hallImages);

    HallAvailabilityDTO checkAvailability(int hallId, LocalDate date);
}
