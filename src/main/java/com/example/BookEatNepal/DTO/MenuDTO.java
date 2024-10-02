package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MenuDTO {
    private List<MenuDetail> menuDetails;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
