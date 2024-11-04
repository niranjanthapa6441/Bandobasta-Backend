package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class PackageBookingDetail {
    private int id;
    private String userId;
    private String packageAvailabilityId;
    private PackageDetail packageDetail;
    private String bookedForDate;
    private String requestedDate;
    private String confirmedDate;
    private LocalTime requestedTime;
    private LocalTime confirmedTime;
}
