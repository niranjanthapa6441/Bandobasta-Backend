package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

import java.util.List;

@Data
public class HallRequest {
    private String venueId;
    private String name;
    private String description;
    private int floorNumber;
    private int capacity;
    private double price;
    private String status;
    List<String> amenityIds;
    private String menuId;
}
