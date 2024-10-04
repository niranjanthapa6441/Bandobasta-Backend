package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookingDTO {
    private List<BookingDetail> bookings;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
