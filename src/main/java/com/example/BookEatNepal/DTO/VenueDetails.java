package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VenueDetails {
    private String id;
    private String name;
    private String address;
    private String description;
    private String status;
    private String startingPrice;
    private List<String> venueImagePaths;
    private String maxCapacity;
    private List<String> amenities;
}
