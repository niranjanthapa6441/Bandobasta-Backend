package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Payload.Request.MenuItemSelectionRangeRequest;
import com.example.BookEatNepal.Payload.Request.MenuRequest;
import com.example.BookEatNepal.Service.MenuService;
import com.example.BookEatNepal.Util.RestResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/menu")
public class MenuController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";

    @Autowired
    private MenuService service;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(
            @RequestBody @Valid MenuRequest request
    ) {
        return RestResponse.ok(service.save(request));
    }

    @PostMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @RequestBody MenuRequest request
    ) {
        return RestResponse.ok(service.update(request, id));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return RestResponse.ok(service.delete(id));
    }

    @GetMapping(value ="/findAll",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAll(
            @RequestParam(required = false) String venueId,
            @RequestParam(required = false) String menuType,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(service.findMenuByVenue(venueId,menuType, page, size), "Data Retrieval Successful");
    }
}
