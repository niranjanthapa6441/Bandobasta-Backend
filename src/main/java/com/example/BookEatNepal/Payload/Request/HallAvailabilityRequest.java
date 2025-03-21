package com.example.BookEatNepal.Payload.Request;
import lombok.Builder;
import lombok.Data;
import java.sql.Time;
import java.time.LocalDate;

@Data
@Builder
public class HallAvailabilityRequest {
    private String hallId;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String status;
    private String shift;
    private String startDate;
    private String endDate;
}