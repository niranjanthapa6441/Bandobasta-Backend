package com.example.BookEatNepal.Payload.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TicketOrderDTO {
    private List<TicketDTO> ticketDTOS;
    private int currentPage;
    private int totalElements;
    private int totalPages;
}
