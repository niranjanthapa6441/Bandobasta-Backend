package com.example.BookEatNepal.Payload.Request;

import lombok.Data;


@Data
public class HallBookingRequest {
    private String userId;
    private String hallAvailabilityId;
    private String menuId;
    private String eventType;
}
