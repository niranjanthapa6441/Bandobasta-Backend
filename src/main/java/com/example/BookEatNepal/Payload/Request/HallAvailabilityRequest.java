package com.example.BookEatNepal.Payload.Request;

import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Model.Hall;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class HallAvailabilityRequest {
    private String hallId;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
}
