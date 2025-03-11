package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HallAvailabilityDTO {
    private List<HallAvailabilityDetail> hallAvailabilityDetails;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}