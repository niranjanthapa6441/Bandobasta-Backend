package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Payload.DTO.VenueDTO;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Payload.Request.VenueRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface VenueService {
    String save(VenueRequest request, List<MultipartFile> venueImages, MultipartFile licenseImage, MultipartFile panImage);

    String delete(int id);

    VenueDTO findAvailableVenues(String venueName,String checkAvailableDate,int numberOfGuests,String location,int minCapacity, int maxCapacity,double minPrice, double maxPrice,double rating, int page, int size);

    VenueDTO findAll(String venueName,String location,int minCapacity, int maxCapacity,double minPrice, double maxPrice,String venueType, double rating, int page, int size);

    Venue findById(int id);

    String update(VenueRequest request, int id, MultipartFile licenseImage, MultipartFile panImage);

    VenueDTO findByOwner(String venueName, String location, int minCapacity, int maxCapacity, double minPrice, double maxPrice, String ownerId, double rating, int page, int size);
}
