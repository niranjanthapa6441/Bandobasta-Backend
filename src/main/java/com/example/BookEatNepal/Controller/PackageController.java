package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Request.PackageRequest;
import com.example.BookEatNepal.Service.PackageService;
import com.example.BookEatNepal.Util.RestResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/package")
public class PackageController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";

    @Autowired
    private PackageService service;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(
            @RequestBody @Valid PackageRequest request
    ) {
        return RestResponse.ok(service.save(request));
    }

    @PostMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @RequestBody PackageRequest request
    ) {
        return RestResponse.ok(service.update(request, id));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return RestResponse.ok(service.delete(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAll(
            @RequestParam(required = true) String venueId,
            @RequestParam(required = false) String packageType,
            @RequestParam(required = false) String eventType,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(service.findPackageByVenue(venueId, packageType, eventType, page, size), "Data Retrieval Successful");
    }
}
