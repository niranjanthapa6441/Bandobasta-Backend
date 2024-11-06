package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

import java.util.List;


@Data
public class BookingRequest {
    private String userId;
    private String id;
    private String menuId;
    private String eventType;
    private int numberOfGuests;
    private List<String> foodIds;
}
