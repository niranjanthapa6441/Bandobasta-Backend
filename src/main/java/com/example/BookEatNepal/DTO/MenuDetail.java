package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MenuDetail {
    private String id;
    private String venueId;
    private String description;
    private double price;
    private String status;
    private String menuType;
    private List<FoodDetail> foodDetails;
}
