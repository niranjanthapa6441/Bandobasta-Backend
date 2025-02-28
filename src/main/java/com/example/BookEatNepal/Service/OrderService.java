package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Payload.DTO.CountOfBookedAndCheckedInTicketsEventDTO;
import com.example.BookEatNepal.Payload.DTO.OrderDTO;
import com.example.BookEatNepal.Payload.DTO.TicketDTO;
import com.example.BookEatNepal.Payload.DTO.TicketOrderDTO;
import com.example.BookEatNepal.Payload.Request.OrderRequest;
import com.example.BookEatNepal.Payload.Request.PaymentRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    OrderDTO save(OrderRequest request);
    String confirmPayment(int orderId, PaymentRequest paymentRequest);
    String checkIn(int ticketOrderId);
    List<TicketDTO> findAllOrdersByUser(String email);
    TicketOrderDTO findAllTicketOrderByEvent(String eventId,String ticketOrderId,int page, int size);
    CountOfBookedAndCheckedInTicketsEventDTO countOfBookedAndCheckedInTicket(String eventId);
}
