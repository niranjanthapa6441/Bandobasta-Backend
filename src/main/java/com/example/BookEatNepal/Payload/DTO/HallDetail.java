package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HallDetail {
    private String venueId;
    private int id;
    private String name;
    private String description;
    private int floorNumber;
    private int capacity;
    private String status;
    private List<String> hallImagePaths;
}
