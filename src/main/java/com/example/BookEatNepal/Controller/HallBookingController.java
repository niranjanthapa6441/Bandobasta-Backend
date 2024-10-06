package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Request.HallBookingRequest;
import com.example.BookEatNepal.Service.HallBookingService;
import com.example.BookEatNepal.Util.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/booking")
public class HallBookingController {

    public static final String SIZE = "5";
    public static final String PAGE = "1";

    @Autowired
    private HallBookingService hallBookingService;

    @PostMapping(value="/hallBooking",produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> save(
            @RequestBody HallBookingRequest request
            ) {
        return RestResponse.ok(hallBookingService.save(request));
    }

    @PostMapping(value = "/hallBooking/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @RequestBody HallBookingRequest request
    ) {
        return RestResponse.ok(hallBookingService.update(request, id));
    }

    @DeleteMapping(value = "/hallBooking/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return RestResponse.ok(hallBookingService.delete(id));
    }

    @GetMapping(value = "/hallBooking/venue",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllBookingByVenue(
            @RequestParam(required = true) String venueId,
            @RequestParam(required = false) String hallId,
            @RequestParam(required = false) String bookingDate,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(hallBookingService.findBookingByVenue(venueId,bookingDate, hallId ,page, size), "Data Retrieval Successful");
    }
    @GetMapping(value="/hallBooking/user",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllBookingByUser(
            @RequestParam(required = true) String userId,
            @RequestParam(required = false) String bookingDate,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(hallBookingService.findBookingByUser(userId,bookingDate ,page, size), "Data Retrieval Successful");
    }
    @PostMapping(value = "/hallBooking/confirm/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> confirmHallBooking(
            @PathVariable int id
    ) {
        return RestResponse.ok(hallBookingService.confirmBooking(id));
    }

}
