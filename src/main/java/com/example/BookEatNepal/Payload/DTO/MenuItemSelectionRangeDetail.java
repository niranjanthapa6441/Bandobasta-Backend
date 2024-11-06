package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class MenuItemSelectionRangeDetail {
    private int maxSelection;
    private String foodSubCategory;
}
