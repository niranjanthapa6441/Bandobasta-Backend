package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Payload.Request.PackageBookingRequest;
import com.example.BookEatNepal.Service.PackageBookingService;
import com.example.BookEatNepal.Util.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/booking")
public class PackageBookingController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";

    @Autowired
    private PackageBookingService packageBookingService;

    @PostMapping(value="/packageBooking",produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> save(
            @RequestBody PackageBookingRequest request
    ) {
        return RestResponse.ok(packageBookingService.save(request));
    }

    @PostMapping(value = "/packageBooking/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @RequestBody PackageBookingRequest request
    ) {
        return RestResponse.ok(packageBookingService.update(request, id));
    }

    @DeleteMapping(value = "/packageBooking/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return RestResponse.ok(packageBookingService.delete(id));
    }

    @GetMapping(value = "/packageBooking/venue",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllBookingByVenue(
            @RequestParam(required = true) String venueId,
            @RequestParam(required = false) String hallId,
            @RequestParam(required = false) String bookingDate,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(packageBookingService.findBookingByVenue(venueId,bookingDate, hallId ,page, size), "Data Retrieval Successful");
    }
    @GetMapping(value="/packageBooking/user",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllBookingByUser(
            @RequestParam(required = true) String userId,
            @RequestParam(required = false) String bookingDate,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(packageBookingService.findBookingByUser(userId,bookingDate ,page, size), "Data Retrieval Successful");
    }
    @PostMapping(value = "/packageBooking/confirm/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> confirmHallBooking(
            @PathVariable int id
    ) {
        return RestResponse.ok(packageBookingService.confirmBooking(id));
    }
}
