package com.example.BookEatNepal.Service;


import com.example.BookEatNepal.DTO.AmenityDTO;
import com.example.BookEatNepal.DTO.AmenityDetails;
import com.example.BookEatNepal.Request.AmenityRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public interface AmenityService {
    String save(AmenityRequest request, MultipartFile image);
    String delete(int id);

    AmenityDTO findAll(String venueId, int page, int size);

    AmenityDetails findById(int id);

    String update(AmenityRequest request, int id, MultipartFile image);
}
