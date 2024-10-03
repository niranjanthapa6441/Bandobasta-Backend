package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Request.HallAvailabilityRequest;
import com.example.BookEatNepal.Request.HallRequest;
import com.example.BookEatNepal.Service.HallService;
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
@RequestMapping("/hall")
public class HallController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";

    @Autowired
    private HallService service;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> save(
            @RequestPart("hall") @Valid HallRequest request,
            @RequestPart("hallImages") List<MultipartFile> hallImages
    ) {
        return RestResponse.ok(service.save(request, hallImages));
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveAvailability(
            @RequestBody List<HallAvailabilityRequest> requests
            ) {
        return RestResponse.ok(service.saveHallAvailability(requests));
    }

    @PostMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @RequestPart("hall") @Valid HallRequest request,
            @RequestPart("hallImages") List<MultipartFile> hallImages
    ) {
        return RestResponse.ok(service.update(request, id, hallImages));
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
