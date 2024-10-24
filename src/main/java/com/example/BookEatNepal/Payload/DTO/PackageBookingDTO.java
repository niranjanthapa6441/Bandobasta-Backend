package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PackageBookingDTO {
    private List<PackageBookingDetail> bookings;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
