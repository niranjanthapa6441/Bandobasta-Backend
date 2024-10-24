package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MenuRequest {
    private String venueId;
    private String description;
    private String menuType;
    private double price;
    private List<String> foodIds;
    private String status;
}
