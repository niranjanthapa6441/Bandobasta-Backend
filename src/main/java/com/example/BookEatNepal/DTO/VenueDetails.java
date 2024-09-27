package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VenueDetails {
    private String name;
    private String address;
    private String description;
    private String status;
    private List<String> venueImagePaths;
}
