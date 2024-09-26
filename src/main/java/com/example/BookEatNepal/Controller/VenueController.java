package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Request.VenueRequest;
import com.example.BookEatNepal.Service.VenueService;
import com.example.BookEatNepal.Util.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestPart;
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
            @RequestPart("registrationImage") MultipartFile registrationImage
    ) {
        request.setVenueImages(images);
        request.setLicenseImage(licenseImage);
        request.setPanImage(request.getPanImage());
        return RestResponse.ok(service.save(request));
    }
}
