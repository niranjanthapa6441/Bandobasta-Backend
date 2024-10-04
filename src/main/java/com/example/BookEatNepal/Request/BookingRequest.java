package com.example.BookEatNepal.Request;

import lombok.Data;


@Data
public class BookingRequest {
    private String userId;
    private String hallAvailabilityId;
    private String packageId;
    private double price;
    private String status;
    private String menuId;
    private String bookedForDate;
    private String eventType;
}
