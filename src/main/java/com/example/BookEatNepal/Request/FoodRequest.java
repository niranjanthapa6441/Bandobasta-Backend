package com.example.BookEatNepal.Request;

import lombok.Data;

@Data
public class FoodRequest {
    private String venueId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String status;
}
