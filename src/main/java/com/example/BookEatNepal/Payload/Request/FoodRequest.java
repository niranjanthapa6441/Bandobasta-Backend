package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

@Data
public class FoodRequest {
    private String venueId;
    private String name;
    private String description;
    private String status;
    private String foodSubCategory;
}
