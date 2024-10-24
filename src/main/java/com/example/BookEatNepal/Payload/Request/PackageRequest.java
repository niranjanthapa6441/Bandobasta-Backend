package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

import java.util.List;

@Data
public class PackageRequest {
    private String venueId;
    private String name;
    private String packageType;
    private String eventType;
    private String description;
    private double price;
    List<String> amenityIds;
    private String hallId;
    private String menuId;
    private String status;
}
