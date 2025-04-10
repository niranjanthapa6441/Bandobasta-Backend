package com.example.BookEatNepal.Controller;
import com.example.BookEatNepal.Payload.Request.HallAvailabilityRequest;
import com.example.BookEatNepal.Payload.Request.HallRequest;
import com.example.BookEatNepal.Service.HallService;
import com.example.BookEatNepal.Util.RestResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/hall")
public class HallController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";
    public static final String NUMBER_OF_GUESTS = "5";
    public static final String HALL_ID = "0";

    @Autowired
    private HallService service;

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> save(
            @RequestPart("hall") @Valid HallRequest request,
            @RequestPart("hallImages") List<MultipartFile> hallImages
    ) {
        return RestResponse.ok(service.save(request, hallImages));
    }

    @PostMapping(value = "/availability/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveAvailability(
            @RequestBody List<HallAvailabilityRequest> requests
    ) {
        return RestResponse.ok(service.saveHallAvailability(requests));
    }

    @GetMapping(value = "/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkAvailability(
            @RequestParam(required = true) String venueId,
            @RequestParam(required = true) String date,
            @RequestParam(required = false, defaultValue = HALL_ID) int hallId,
            @RequestParam(defaultValue = NUMBER_OF_GUESTS) int numberOfGuests,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(service.checkAvailability(venueId, hallId, date, numberOfGuests, page, size), "Data Retrieval Successful");
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

    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAll(
            @RequestParam(required = true) String venueId,
            @RequestParam(required = false) String checkAvailableDate,
            @RequestParam(defaultValue = NUMBER_OF_GUESTS) int numberOfGuests,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size

    ) {
        return RestResponse.ok(service.findAll(venueId, numberOfGuests, page, size, checkAvailableDate), "Data Retrieval Successful");
    }

    @PostMapping("/availability/update")
    private ResponseEntity<Object> updateHallAvailability(@RequestParam String shift,
                                                          @RequestParam String date,
                                                          @RequestParam String status) {
        return RestResponse.ok(service.updateHallAvailability(shift, status, LocalDate.parse(date)));
    }
}