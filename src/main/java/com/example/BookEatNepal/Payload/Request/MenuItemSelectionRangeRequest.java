package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

@Data
public class MenuItemSelectionRangeRequest {
    private String foodSubCategory;
    private int maxSelection;
}
