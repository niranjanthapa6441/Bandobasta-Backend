package com.example.BookEatNepal.Request;

import com.example.BookEatNepal.Enums.AmenityStatus;
import com.example.BookEatNepal.Model.Venue;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class AmenityRequest {
    private String venueId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String status;
}
