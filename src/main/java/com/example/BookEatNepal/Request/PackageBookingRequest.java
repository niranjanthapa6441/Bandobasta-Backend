package com.example.BookEatNepal.Request;

import lombok.Data;

@Data
public class PackageBookingRequest {
    private String userId;
    private String packageAvailabilityId;
    private String status;
    private String bookedForDate;
    private String eventType;
}
