package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

@Data
public class PackageAvailabilityRequest {
    private String packageId;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
}
