package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

import java.util.List;

@Data
public class AmenityRequest {
    private String venueId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String status;
}

