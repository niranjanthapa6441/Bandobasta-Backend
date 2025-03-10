package com.example.BookEatNepal.Payload.DTO;

import com.example.BookEatNepal.Enums.HallShift;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HallAvailabilityDetail {
    private String id;
    private String venueName;
    private String hallId;
    private String hallName;
    private String description;
    private String date;
    @Enumerated(EnumType.STRING)
    private HallShift shift;
    private int capacity;
    private String status;
}