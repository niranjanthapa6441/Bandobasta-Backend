package com.example.BookEatNepal.Service;


import com.example.BookEatNepal.Payload.DTO.AmenityDTO;
import com.example.BookEatNepal.Payload.DTO.AmenityDetail;
import com.example.BookEatNepal.Payload.Request.AmenityRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AmenityService {
    String save(AmenityRequest request, MultipartFile image);
    String delete(int id);

    AmenityDTO findAll(String venueId, int page, int size);

    AmenityDetail findById(int id);

    String update(AmenityRequest request, int id, MultipartFile image);
}
