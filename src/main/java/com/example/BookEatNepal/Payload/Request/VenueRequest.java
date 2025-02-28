package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

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
    private String permanentAccountNumber;
    private String description;
    private String licenseImagePath;
    private String panImagePath;
    private double menuPrice;
}
