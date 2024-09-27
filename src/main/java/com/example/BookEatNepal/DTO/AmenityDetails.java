package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmenityDetails {
    private String id;
    private String venueId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String status;
}
