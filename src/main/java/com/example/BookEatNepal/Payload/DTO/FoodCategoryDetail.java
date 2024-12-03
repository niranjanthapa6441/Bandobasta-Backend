package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FoodCategoryDetail {
    String category;
    Map<Integer,String> subCategory;
}
