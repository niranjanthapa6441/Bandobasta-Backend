package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PackageDetail {
    private String id;
    private String venueId;
    private String name;
    private String packageType;
    private String eventType;
    private String description;
    private double price;
    List<String> amenities;
    private HallDetail hallDetail;
    private MenuDetail menuDetail;
    private String status;
}
