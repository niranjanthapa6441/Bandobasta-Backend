    package com.example.BookEatNepal.Request;
    import lombok.Data;

    @Data
    public class HallRequest {
        private String venueId;
        private String name;
        private String description;
        private int floorNumber;
        private int capacity;
        private double price;
        private String status;
    }
