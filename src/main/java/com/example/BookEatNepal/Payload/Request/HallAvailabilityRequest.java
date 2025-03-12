package com.example.BookEatNepal.Payload.Request;
import lombok.Builder;
import lombok.Data;
import java.sql.Time;
@Data
@Builder
public class HallAvailabilityRequest {
    private String hallId;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
    private String shift;
}