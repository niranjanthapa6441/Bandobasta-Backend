package com.example.BookEatNepal.Payload.Request;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class HallAvailabilityRequest {
        private String hallId;
        private String startDate;
        private String endDate;
        private List<Shift> shift;
        private String status;

        @Data
        @Builder
        public static class Shift {
            private String startTime;
            private String endTime;
            private String shift;
        }
}