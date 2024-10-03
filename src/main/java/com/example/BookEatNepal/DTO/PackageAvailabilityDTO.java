package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PackageAvailabilityDTO {
    private List<PackageAvailabilityDetail> packageAvailabilityDetails;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
