package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Payload.Request.OrderRequest;
import com.example.BookEatNepal.Payload.Request.PaymentRequest;
import com.example.BookEatNepal.Service.OrderService;
import com.example.BookEatNepal.Util.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/event/order/")
public class EventOrderController {
    public static final String SIZE = "5";
    public static final String PAGE = "1";

    @Autowired
    private OrderService service;

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(
            @RequestBody OrderRequest request
            ) {
        return RestResponse.ok(service.save(request));
    }

    @PostMapping(value = "/confirmOrder", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> confirmOrder(
            @RequestParam ( required = true) int orderId,
            @RequestBody PaymentRequest request
    ) {
        return RestResponse.ok(service.confirmPayment(orderId,request));
    }

    @PostMapping(value = "/checkIn", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkIn(
            @RequestParam ( required = true) int ticketOrderId
    ) {
        return RestResponse.ok(service.checkIn(ticketOrderId ));
    }

    @GetMapping(value = "/findAllByUser",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findOrderByUser(
            @RequestParam(required = true) String email
    ) {
        return RestResponse.ok(service.findAllOrdersByUser(email), "Data Retrieval Successful");
    }

    @GetMapping(value = "/findAllTicketOrderByEvent",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllTicketOrderByEvent(
            @RequestParam(required = true) String eventId,
            @RequestParam(required = false) String ticketOrderId,
            @RequestParam(defaultValue = PAGE) int page,
            @RequestParam(defaultValue = SIZE) int size
    ) {
        return RestResponse.ok(service.findAllTicketOrderByEvent(eventId,ticketOrderId,page,size), "Data Retrieval Successful");
    }

    @GetMapping(value = "/countOfBookedAndCheckedInTicket",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> countOfBookedAndCheckedInTicket(
            @RequestParam(required = true) String eventId
    ) {
        return RestResponse.ok(service.countOfBookedAndCheckedInTicket(eventId), "Data Retrieval Successful");
    }
}
