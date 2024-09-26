package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Request.VenueRequest;
import com.example.BookEatNepal.Service.VenueService;
import com.example.BookEatNepal.Util.CustomException;
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
public class VenueController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";

    public static final String MESSAGE = "Successful";
    @Autowired
    private VenueService service;
    @PostMapping(value = "/venues", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> save(
            @RequestPart("venue") @Valid VenueRequest request,
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart("licenseImage") MultipartFile licenseImage,
            @RequestPart("registrationImage") MultipartFile panImage
    ) {
        request.setVenueImages(images);
        request.setLicenseImage(licenseImage);
        request.setPanImage(panImage);
        return RestResponse.ok(service.save(request));
    }
    @PostMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateVenue(
            @PathVariable int id,
            @RequestPart("venue") @Valid VenueRequest request,
            @RequestPart("licenseImage") MultipartFile licenseImage,
            @RequestPart("registrationImage") MultipartFile panImage
            ) {
        request.setLicenseImage(licenseImage);
        request.setPanImage(panImage);
        return RestResponse.ok(service.update(request, id));
    }
    @DeleteMapping(value = "/{id}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return RestResponse.ok(service.delete(id));
    }
}
