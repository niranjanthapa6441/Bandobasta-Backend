package com.example.BookEatNepal.Payload.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO {
    private List<OrderDetail> orderDetails;
    private EventUserDTO user;

    @Data
    public static class OrderDetail{
        private int orderId;
        private String orderStatus;
        private LocalDate orderDate;
        private double totalAmount;
        private List<TicketDetail> ticketOrders;
    }
    @Data
    public static class EventUserDTO {
        private String fullName;
        private String email;
        private String phoneNumber;
    }
    @Data
    public static class TicketDetail{
        private int ticketOrderId;
        private LocalDate eventDate;
        private String ticketType;
    }
}

