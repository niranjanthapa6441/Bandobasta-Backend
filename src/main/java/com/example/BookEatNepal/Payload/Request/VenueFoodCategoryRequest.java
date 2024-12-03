package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

import java.util.List;

@Data
public class VenueFoodCategoryRequest {
    String category;
    List<String> subCategories;
}
