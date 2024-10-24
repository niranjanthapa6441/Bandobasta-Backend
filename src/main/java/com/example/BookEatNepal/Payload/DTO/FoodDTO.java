package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FoodDTO {
    private List<FoodDetail> foodDetails;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
