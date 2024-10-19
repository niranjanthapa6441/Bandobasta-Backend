package com.example.BookEatNepal.Request;

import lombok.Data;


@Data
public class HallBookingRequest {
    private String userId;
    private String hallAvailabilityId;
    private double price;
    private String status;
    private String menuId;
    private String eventType;
}
