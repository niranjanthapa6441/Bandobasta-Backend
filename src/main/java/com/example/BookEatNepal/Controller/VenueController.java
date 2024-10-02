package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Request.VenueRequest;
import com.example.BookEatNepal.Service.VenueService;
import com.example.BookEatNepal.Util.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/venue")
public class VenueController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";
    public static final String RATING = "0.0";
    public static final String MAX_CAPACITY = "0";
    public static final String MIN_CAPACITY = "1000";
    public static final String MESSAGE = "Successful";
    @Autowired
    private VenueService service;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> save(
            @RequestPart("venue") @Valid VenueRequest request,
            @RequestPart("venueImages") List<MultipartFile> venueImages,
            @RequestPart("licenseImage") MultipartFile licenseImage,
            @RequestPart("panImage") MultipartFile panImage
    ) {
        return RestResponse.ok(service.save(request, venueImages, licenseImage, panImage));
    }


    @PostMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @RequestPart("venue") @Valid VenueRequest request,
            @RequestPart("licenseImage") MultipartFile licenseImage,
            @RequestPart("panImage") MultipartFile panImage
    ) {
        return RestResponse.ok(service.update(request, id, licenseImage, panImage));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return RestResponse.ok(service.delete(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = MAX_CAPACITY) int maxCapacity,
            @RequestParam(defaultValue = MIN_CAPACITY) int minCapacity,
            @RequestParam(required = false) String venueType,
            @RequestParam(defaultValue = RATING) double rating,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(service.findAll(name, location,maxCapacity,minCapacity, venueType, rating, page, size), "Data Retrieval Successful");
    }
}
