package com.example.BookEatNepal.Service;


import com.example.BookEatNepal.Payload.DTO.FoodDTO;
import com.example.BookEatNepal.Payload.DTO.FoodDetail;
import com.example.BookEatNepal.Payload.Request.FoodRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FoodService {

    String save(List<FoodRequest> requests);
    String delete(int id);

    FoodDTO findAll(String venueId, int page, int size);

    FoodDetail findById(int id);

    String update(FoodRequest request, int id, MultipartFile image);
}
