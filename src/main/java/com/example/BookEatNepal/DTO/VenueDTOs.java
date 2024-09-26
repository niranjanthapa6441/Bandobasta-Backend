package com.example.BookEatNepal.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public class VenueDTOs {
    private List<VenueDTO> venues;

    private int currentPage;

    private long totalElements;

    private int totalPages;
}
