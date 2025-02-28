package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Service.TicketService;
import com.example.BookEatNepal.Util.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/event/ticket/")
public class TicketController {

    @Autowired
    private TicketService service;

    @GetMapping(value = "/findAll",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findOrderByUser(
            @RequestParam(required = true) int eventId
    ) {
        return RestResponse.ok(service.findAll(eventId), "Data Retrieval Successful");
    }
}
