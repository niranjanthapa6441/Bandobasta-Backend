package com.example.BookEatNepal.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public class VenueDTO {
    private List<VenueDetails> venues;

    private int currentPage;

    private long totalElements;

    private int totalPages;
}
