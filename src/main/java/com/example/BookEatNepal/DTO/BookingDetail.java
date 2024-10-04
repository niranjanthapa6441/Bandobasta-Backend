package com.example.BookEatNepal.DTO;

import com.example.BookEatNepal.Enums.BookingStatus;
import com.example.BookEatNepal.Enums.EventType;
import com.example.BookEatNepal.Model.AppUser;
import com.example.BookEatNepal.Model.HallAvailability;
import com.example.BookEatNepal.Model.Menu;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class BookingDetail {
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
