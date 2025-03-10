package com.example.BookEatNepal.Payload.Request;

import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Model.Hall;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class HallAvailabilityRequest {
    private String hallId;
    private String date;
    private Time startTime;
    private Time endTime;
    private String status;
    private String shift;
}