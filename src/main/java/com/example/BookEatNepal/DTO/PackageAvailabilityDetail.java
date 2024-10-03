package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PackageAvailabilityDetail {
    private String id;
    private String packageId;
    private String packageName;
    private String description;
    private String date;
    private String startTime;
    private String endTime;
    private int capacity;
    private String status;
}
