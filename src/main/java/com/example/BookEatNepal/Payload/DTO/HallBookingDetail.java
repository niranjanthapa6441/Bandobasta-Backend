package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

@Data
@Builder
public class HallBookingDetail {
    private int id;
    private String venueName;
    private String userId;
    private String bookedForDate;
    private HallDetail hallDetail;
    private MenuDetail menuDetail;
    private String requestedDate;
    private String confirmedDate;
    private LocalTime requestedTime;
    private LocalTime confirmedTime;
    private double price;
    private String status;
    private String eventType;
    private Time startTime;
    private Time     endTime;
    private int numberOfGuests;
}
