package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

@Data
public class BookingDateRequest {
    private String venueId;
    private String userId;
    private String requestedDate;
    private String timeSlot;
    private String message;
    private int numberOfGuests;
}
