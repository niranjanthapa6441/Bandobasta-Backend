package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Enums.OrderStatus;
import com.example.BookEatNepal.Enums.PaymentStatus;
import com.example.BookEatNepal.Model.*;
import com.example.BookEatNepal.Model.Order;
import com.example.BookEatNepal.Payload.DTO.CountOfBookedAndCheckedInTicketsEventDTO;
import com.example.BookEatNepal.Payload.DTO.OrderDTO;
import com.example.BookEatNepal.Payload.DTO.TicketDTO;
import com.example.BookEatNepal.Payload.DTO.TicketOrderDTO;
import com.example.BookEatNepal.Payload.Request.OrderRequest;
import com.example.BookEatNepal.Payload.Request.PaymentRequest;
import com.example.BookEatNepal.Payload.Request.TicketDetail;
import com.example.BookEatNepal.Repository.*;
import com.example.BookEatNepal.Service.OrderService;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl implements OrderService {
    private static final String SUCCESS_MESSAGE = "successful";

    @Autowired
    private EventUserRepo userRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private TicketOrderRepo ticketOrderRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private TicketPaymentRepo ticketPaymentRepo;

    @Autowired
    EntityManager entityManager;

    @Override
    public OrderDTO save(OrderRequest request) {
        validateOrder(request);

        EventUser eventUser = saveEventUser(request);
        List<Order> orders = new ArrayList<>();
        for (TicketDetail ticketDetail : request.getTicketDetails()) {
            Order order = saveOrder(request, eventUser, ticketDetail);
            saveTicketOrders(order, ticketDetail);
            orders.add(order);
        }

       return toOrderDTO(eventUser,orders);
    }


    @Override
    public String confirmPayment(int orderId, PaymentRequest paymentRequest) {
        Order order = getOrder(orderId);
        order.setOrderStatus(OrderStatus.BOOKED);
        orderRepo.save(order);

        toTicketOrder(order);
        toPayment(paymentRequest,order);

        deductAvailableTicket(order);
        return SUCCESS_MESSAGE;
    }

    private void toTicketOrder(Order order) {
        List<TicketOrder> ticketOrders = ticketOrderRepo.findByOrder(order);
        for (TicketOrder ticketOrder: ticketOrders){
            ticketOrder.setOrderStatus(OrderStatus.BOOKED);
            ticketOrderRepo.save(ticketOrder);
        }
    }

    @Override
    public String checkIn(int ticketOrderId) {
        TicketOrder ticketOrder = ticketOrderRepo.findById(ticketOrderId).orElseThrow( ()-> new CustomException(CustomException.Type.ORDER_TICKET_NOT_FOUND));

        if (ticketOrder.getOrderStatus().equals(OrderStatus.CHECKED_IN)){
            throw new CustomException(CustomException.Type.TICKET_HAS_ALREADY_BEEN_CHECKED_IN);
        }

        ticketOrder.setOrderStatus(OrderStatus.CHECKED_IN);
        ticketOrderRepo.save(ticketOrder);

        Order order=getOrder(ticketOrder.getOrder().getId());
        order.setOrderStatus(OrderStatus.CHECKED_IN);

        orderRepo.save(order);


        return SUCCESS_MESSAGE;
    }

    @Override
    public List<TicketDTO> findAllOrdersByUser(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> orderRoot = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(orderRoot.get("eventUser").get("email"), email));

        query.select(orderRoot).where(predicates.toArray(new Predicate[0]));

        List<Order> orders = entityManager.createQuery(query).getResultList();

        return orders.stream()
                .map(order -> {
                    TicketDTO dto = new TicketDTO();
                    dto.setOrderId(order.getId());
                    dto.setUserId(order.getEventUser().getId());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setTotalAmount(order.getTotalAmount());

                    List<TicketDTO.TicketDetailDTO> ticketDetails = fetchTicketDetailsForOrder(order.getId());
                    dto.setTicketDetails(ticketDetails);

                    dto.setNumberOfTickets(ticketDetails.size());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TicketOrderDTO findAllTicketOrderByEvent(String eventId,String ticketOrderId, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketOrder> query = cb.createQuery(TicketOrder.class);
        Root<TicketOrder> ticketOrderRoot = query.from(TicketOrder.class);

        Join<TicketOrder, Ticket> ticketJoin = ticketOrderRoot.join("ticket");
        Join<Ticket, Event> eventJoin = ticketJoin.join("event");

        query.select(ticketOrderRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (eventId != null && !eventId.isEmpty()) {
            try {
                int id = Integer.parseInt(eventId);
                predicates.add(cb.equal(eventJoin.get("id"), id));
            } catch (NumberFormatException e) {
                throw new CustomException(CustomException.Type.EVENT_NOT_FOUND);
            }
        }
        if (ticketOrderId != null && !ticketOrderId.isEmpty()) {
            try {
                int id = Integer.parseInt(ticketOrderId);
                predicates.add(cb.equal(ticketOrderRoot.get("id"), id));
            } catch (NumberFormatException e) {
                throw new CustomException(CustomException.Type.INVALID_TICKET_ORDER_ID);
            }
        }

        predicates.add(ticketOrderRoot.get("orderStatus").in(OrderStatus.BOOKED, OrderStatus.CHECKED_IN));

        query.where(predicates.toArray(new Predicate[0]));

        query.orderBy(cb.asc(ticketOrderRoot.get("id")));

        List<TicketOrder> allTicketOrders = entityManager.createQuery(query).getResultList();

        TypedQuery<TicketOrder> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);
        List<TicketOrder> pagedTicketOrders = typedQuery.getResultList();

        int totalElements = allTicketOrders.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return convertToTicketOrderDTO(pagedTicketOrders, page, totalElements, totalPages);
    }

    @Override
    public CountOfBookedAndCheckedInTicketsEventDTO countOfBookedAndCheckedInTicket(String eventId) {
        if (eventId == null || eventId.isEmpty()) {
            throw new CustomException(CustomException.Type.EVENT_NOT_FOUND);
        }

        try {
            int id = Integer.parseInt(eventId);

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            // Count for both BOOKED and CHECKED_IN tickets (totalBooked)
            CriteriaQuery<Long> totalBookedQuery = cb.createQuery(Long.class);
            Root<TicketOrder> totalBookedRoot = totalBookedQuery.from(TicketOrder.class);
            Join<TicketOrder, Ticket> totalBookedTicketJoin = totalBookedRoot.join("ticket");
            Join<Ticket, Event> totalBookedEventJoin = totalBookedTicketJoin.join("event");

            totalBookedQuery.select(cb.count(totalBookedRoot));
            totalBookedQuery.where(
                    cb.and(
                            cb.equal(totalBookedEventJoin.get("id"), id),
                            totalBookedRoot.get("orderStatus").in(OrderStatus.BOOKED, OrderStatus.CHECKED_IN)
                    )
            );
            long totalBooked = entityManager.createQuery(totalBookedQuery).getSingleResult();

            // Count for CHECKED_IN tickets
            CriteriaQuery<Long> checkedInQuery = cb.createQuery(Long.class);
            Root<TicketOrder> checkedInRoot = checkedInQuery.from(TicketOrder.class);
            Join<TicketOrder, Ticket> checkedInTicketJoin = checkedInRoot.join("ticket");
            Join<Ticket, Event> checkedInEventJoin = checkedInTicketJoin.join("event");

            checkedInQuery.select(cb.count(checkedInRoot));
            checkedInQuery.where(
                    cb.and(
                            cb.equal(checkedInEventJoin.get("id"), id),
                            cb.equal(checkedInRoot.get("orderStatus"), OrderStatus.CHECKED_IN)
                    )
            );
            long totalCheckedIn = entityManager.createQuery(checkedInQuery).getSingleResult();

            // Return the result
            CountOfBookedAndCheckedInTicketsEventDTO result = new CountOfBookedAndCheckedInTicketsEventDTO();
            result.setTotalBooked((int) totalBooked);
            result.setTotalCheckedIn((int) totalCheckedIn);
            return result;

        } catch (NumberFormatException e) {
            throw new CustomException(CustomException.Type.EVENT_NOT_FOUND);
        }
    }


    private TicketOrderDTO convertToTicketOrderDTO(List<TicketOrder> ticketOrders, int currentPage, int totalElements, int totalPages) {
        List<TicketDTO> ticketDTOs = ticketOrders.stream().map(ticketOrder -> {
            TicketDTO ticketDTO = new TicketDTO();
            Order order = ticketOrder.getOrder();
            Ticket ticket = ticketOrder.getTicket();

            ticketDTO.setOrderId(order.getId());
            ticketDTO.setUserId(order.getEventUser().getId());
            ticketDTO.setOrderDate(order.getOrderDate());
            ticketDTO.setTotalAmount(order.getTotalAmount());
            ticketDTO.setNumberOfTickets(order.getNumberOfTickets());

            TicketDTO.TicketDetailDTO ticketDetailDTO = new TicketDTO.TicketDetailDTO();
            ticketDetailDTO.setTicketOrderId(ticketOrder.getId());
            ticketDetailDTO.setTicketType(ticket.getTicketType());
            ticketDetailDTO.setEventDate(ticket.getEvent().getDate());
            ticketDetailDTO.setOrderStatus(ticketOrder.getOrderStatus().name());

            ticketDTO.setTicketDetails(List.of(ticketDetailDTO));
            return ticketDTO;
        }).collect(Collectors.toList());

        TicketOrderDTO ticketOrderDTO = new TicketOrderDTO();
        ticketOrderDTO.setTicketDTOS(ticketDTOs);
        ticketOrderDTO.setCurrentPage(currentPage);
        ticketOrderDTO.setTotalElements(totalElements);
        ticketOrderDTO.setTotalPages(totalPages);

        return ticketOrderDTO;
    }




    private List<TicketDTO.TicketDetailDTO> fetchTicketDetailsForOrder(int orderId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketOrder> query = cb.createQuery(TicketOrder.class);
        Root<TicketOrder> ticketOrderRoot = query.from(TicketOrder.class);
        Join<TicketOrder, Ticket> ticketJoin = ticketOrderRoot.join("ticket");

        query.select(ticketOrderRoot)
                .where(cb.equal(ticketOrderRoot.get("order").get("id"), orderId));

        List<TicketOrder> ticketOrders = entityManager.createQuery(query).getResultList();

        return getTicketDetailDTOS(ticketOrders);
    }

    private static List<TicketDTO.TicketDetailDTO> getTicketDetailDTOS(List<TicketOrder> ticketOrders) {
        List<TicketDTO.TicketDetailDTO> ticketDetails = new ArrayList<>();

        for (TicketOrder ticketOrder : ticketOrders) {
            Ticket ticket = ticketOrder.getTicket();
            String ticketType = ticket.getTicketType();

            TicketDTO.TicketDetailDTO detail = new TicketDTO.TicketDetailDTO();
            detail.setTicketOrderId(ticketOrder.getId());
            detail.setTicketType(ticketType);
            detail.setEventDate(ticketOrder.getTicket().getEvent().getDate());
            detail.setOrderStatus(String.valueOf(ticketOrder.getOrderStatus()));
            ticketDetails.add(detail);
        }
        return ticketDetails;
    }


    private void validateOrder(OrderRequest request) {
        if (request.getTicketDetails() == null || request.getTicketDetails().isEmpty()) {
            throw new CustomException(CustomException.Type.INVALID_REQUEST);
        }

        for (TicketDetail ticketDetail : request.getTicketDetails()) {
            Ticket ticket = getTicket(ticketDetail);
            if (ticket.getAvailableQuantity() < ticketDetail.getNumberOfTickets()) {
                throw new CustomException(CustomException.Type.INSUFFICIENT_TICKET_QUANTITY);
            }
        }
    }

    private EventUser saveEventUser(OrderRequest request) {
        EventUser eventUser = new EventUser();
        eventUser.setFullName(request.getFullName());
        eventUser.setEmail(request.getEmail());
        eventUser.setPhoneNumber(request.getPhoneNumber());
        return userRepo.save(eventUser);
    }

    private Order saveOrder(OrderRequest request, EventUser eventUser, TicketDetail ticketDetail) {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);
        order.setEventUser(eventUser);
        order.setOrderDate(LocalDate.now());
        order.setNumberOfTickets(ticketDetail.getNumberOfTickets());
        order.setTotalAmount(calculateTotalAmount(ticketDetail ));
        return orderRepo.save(order);
    }

    private void saveTicketOrders(Order order, TicketDetail ticketDetail) {
        for (int i =0; i< ticketDetail.getNumberOfTickets();i++){
            TicketOrder ticketOrder = new TicketOrder();
            ticketOrder.setTicket(getTicket(ticketDetail));
            ticketOrder.setOrder(order);
            ticketOrder.setOrderStatus(OrderStatus.PENDING);

            ticketOrderRepo.save(ticketOrder);
        }
    }

    private double calculateTotalAmount(TicketDetail ticketDetail) {
        Ticket ticket= getTicket(ticketDetail);
        return ticket.getPrice() * ticketDetail.getNumberOfTickets();
    }

    private Map<Integer, Ticket> mapTicketDetails(List<TicketDetail> ticketDetails) {
        return ticketDetails.stream()
                .collect(Collectors.toMap(TicketDetail::getTicketId, this::getTicket));
    }

    private Ticket getTicket(TicketDetail ticketDetail) {
        return ticketRepo.findById(ticketDetail.getTicketId())
                .orElseThrow(() -> new CustomException(CustomException.Type.TICKET_NOT_FOUND));
    }

    private OrderDTO toOrderDTO(EventUser user, List<Order> orders) {
        OrderDTO orderDTO = new OrderDTO();

        OrderDTO.EventUserDTO userDTO = new OrderDTO.EventUserDTO();
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        orderDTO.setUser(userDTO);

        List<OrderDTO.OrderDetail> orderDetails = new ArrayList<>();

        for (Order order : orders) {
            OrderDTO.OrderDetail orderDetail = new OrderDTO.OrderDetail();
            orderDetail.setOrderId(order.getId());
            orderDetail.setOrderStatus(String.valueOf(order.getOrderStatus()));
            orderDetail.setOrderDate(order.getOrderDate());
            orderDetail.setTotalAmount(order.getTotalAmount());

            List<OrderDTO.TicketDetail> ticketDetails = new ArrayList<>();
            List<TicketOrder> ticketOrders = ticketOrderRepo.findByOrder(order);
            for (TicketOrder ticketOrder : ticketOrders) {
                OrderDTO.TicketDetail ticketDetail = new OrderDTO.TicketDetail();
                ticketDetail.setTicketOrderId(ticketOrder.getId());
                ticketDetail.setTicketType(ticketOrder.getTicket().getTicketType());
                ticketDetail.setEventDate(ticketOrder.getTicket().getEvent().getDate());
                ticketDetails.add(ticketDetail);
            }
            orderDetail.setTicketOrders(ticketDetails);

            orderDetails.add(orderDetail);
        }
        orderDTO.setOrderDetails(orderDetails);

        return orderDTO;
    }


    private Order getOrder(int orderId) {
        return orderRepo.findById(orderId).orElseThrow(() -> new CustomException(CustomException.Type.ORDER_NOT_FOUND));
    }

    private void deductAvailableTicket(Order order) {
        List<TicketOrder> ticketOrders = ticketOrderRepo.findByOrder(order);

        TicketOrder ticketOrder = ticketOrders.get(0); // Select the first ticket order
        Ticket ticket = ticketOrder.getTicket();
        int availableQuantity = ticket.getAvailableQuantity() - order.getNumberOfTickets();

        if (availableQuantity < 0) {
            throw new CustomException(CustomException.Type.INSUFFICIENT_TICKET_QUANTITY);
        }

        ticket.setAvailableQuantity(availableQuantity);
        ticketRepo.save(ticket);
    }


    private void toPayment(PaymentRequest paymentRequest,Order order) {
        TicketPayment ticketPayment= new TicketPayment();
        ticketPayment.setPaymentDate(LocalDate.now());
        ticketPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        ticketPayment.setPaymentPartner(paymentRequest.getPaymentPartner());
        ticketPayment.setOrder(order);
        ticketPayment.setPaidAmount(order.getTotalAmount());
        ticketPayment.setPaymentTime(LocalTime.now());
        ticketPayment.setStatus(PaymentStatus.COMPLETED);

        ticketPaymentRepo.save(ticketPayment);
    }
}
