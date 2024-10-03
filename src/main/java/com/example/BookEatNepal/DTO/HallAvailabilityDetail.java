package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HallAvailabilityDetail {
    private String id;
    private String hallId;
    private String hallName;
    private String description;
    private String date;
    private String startTime;
    private String endTime;
    private int capacity;
    private String status;
}
