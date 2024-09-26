package com.example.BookEatNepal.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class VenueRequest {
    private String ownerId;
    private String name;
    private String country;
    private String state;
    private String district;
    private String city;
    private String streetName;
    private String streetNumber;
    private String email;
    private String primaryPhoneNumber;
    private String secondaryPhoneNumber;
    private String countryCode;
    private String registrationNumber;
    private String licenseNumber;
    private MultipartFile licenseImage;
    private String permanentAccountNumber;
    private MultipartFile panImage;
    private String description;
    private List<MultipartFile> venueImages;
    private String licenseImagePath;
    private String panImagePath;
}
