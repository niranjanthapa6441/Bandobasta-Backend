package com.example.BookEatNepal.Payload.DTO;

import com.example.BookEatNepal.Payload.Request.TicketDetail;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TicketDTO {
    private int orderId;
    private int userId;
    private LocalDate orderDate;
    private double totalAmount;
    private int numberOfTickets;
    private List<TicketDetailDTO> ticketDetails;

    @Data
    public static  class  TicketDetailDTO{
        private int ticketOrderId;
        private String ticketType;
        private LocalDate eventDate;
        private String orderStatus;
    }
}

