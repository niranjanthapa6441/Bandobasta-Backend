package com.example.BookEatNepal.DTO;

import com.example.BookEatNepal.Enums.HallStatus;
import com.example.BookEatNepal.Model.Venue;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HallDetails {
    private String venueId;

    private String name;

    private String description;

    private int floorNumber;

    private int capacity;

    private String status;

    private List<String> hallImagePaths;
}
