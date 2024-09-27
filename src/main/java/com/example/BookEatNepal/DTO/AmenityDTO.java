package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AmenityDTO {
    private List<AmenityDetails> amenityDetails;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
