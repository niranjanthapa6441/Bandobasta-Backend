package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VenueDTO {
    private List<VenueDetails> venues;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
