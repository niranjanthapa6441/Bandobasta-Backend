package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.DTO.VenueDTO;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Request.VenueRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface VenueService {
    String save(VenueRequest venueRequest, List<MultipartFile> venueImages, MultipartFile licenseImage, MultipartFile panImage);

    String delete(int id);


    VenueDTO findAll(String name, String location, double rating, int page, int size);

    Venue findById(int id);

    String update(VenueRequest request, int id, MultipartFile licenseImage, MultipartFile panImage);
}
