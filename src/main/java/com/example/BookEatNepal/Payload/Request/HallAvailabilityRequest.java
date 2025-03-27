package com.example.BookEatNepal.Payload.Request;
import lombok.Builder;
import lombok.Data;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class HallAvailabilityRequest {
        private String hallId;
        private String startDate;
        private String endDate;
        private List<Shift> shift; // A list of Shift objects
        private String status;

        @Data
        @Builder// Apply Lombok to the inner class as well
        public static class Shift {
            private String startTime;
            private String endTime;
            private String shift; // Represents the shift name (e.g., Morning, Evening)
        }
    }