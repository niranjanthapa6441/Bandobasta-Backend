package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PackageDTO {
    private List<PackageDetail> packages;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
