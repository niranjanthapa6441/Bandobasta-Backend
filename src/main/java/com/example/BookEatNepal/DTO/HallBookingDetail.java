package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class HallBookingDetail {
    private int id;
    private String userId;
    private String hallAvailabilityId;
    private HallDetail hallDetail;
    private MenuDetail menuDetail;
    private String requestedDate;
    private String confirmedDate;
    private LocalTime requestedTime;
    private LocalTime confirmedTime;
    private double price;
    private String status;
    private String eventType;
}
