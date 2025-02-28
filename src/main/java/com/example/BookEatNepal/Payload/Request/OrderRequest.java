package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

import java.util.List;

@Data
public class    OrderRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<TicketDetail> ticketDetails;
}
