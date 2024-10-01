package com.example.BookEatNepal.DTO;

import com.example.BookEatNepal.Enums.FoodStatus;
import com.example.BookEatNepal.Model.Venue;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class    FoodDetail {
    private String id;
    private String venueId;
    private String name;
    private String description;
    private String imageUrl;
    private String status;
    private String foodCategory;
}
