package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmenityDetail {
    private String id;
    private String venueId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
//    private String status;
}
