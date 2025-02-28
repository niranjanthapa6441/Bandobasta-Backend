package com.example.BookEatNepal.Payload.Request;

import lombok.Data;

@Data
public class TicketDetail {
    private int ticketId;
    private int numberOfTickets;
    private String ticketType;
}
