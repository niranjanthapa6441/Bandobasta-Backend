package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateBookingRequest {
    private double price;
    private String status;
    private int numberOfGuests;
    private String hallAvailabilityId;
    private String menuId;
    private List<String> foodIds;
}
