package com.example.BookEatNepal.Payload.Request;

import lombok.Data;


@Data
public class BookingRequest {
    private String userId;
    private String id;
    private String menuId;
    private String eventType;
    private int numberOfGuests;
}
