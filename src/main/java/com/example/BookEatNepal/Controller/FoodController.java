package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Payload.Request.FoodRequest;
import com.example.BookEatNepal.Service.FoodService;
import com.example.BookEatNepal.Util.RestResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/food")
public class FoodController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";

    @Autowired
    private FoodService service;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> save(
            @RequestPart("food") @Valid FoodRequest request,
            @RequestPart("foodImage") MultipartFile foodImage
    ) {
        return RestResponse.ok(service.save(request, foodImage));
    }

    @PostMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @RequestPart("food") @Valid FoodRequest request,
            @RequestPart("foodImage") MultipartFile foodImage
    ) {
        return RestResponse.ok(service.update(request, id, foodImage));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return RestResponse.ok(service.delete(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAll(
            @RequestParam(required = true) String venueId,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(service.findAll(venueId, page, size), "Data Retrieval Successful");
    }
}
