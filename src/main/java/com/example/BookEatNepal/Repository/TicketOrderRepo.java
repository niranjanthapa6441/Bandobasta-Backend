package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Order;
import com.example.BookEatNepal.Model.TicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketOrderRepo extends JpaRepository<TicketOrder,Integer> {
    List<TicketOrder> findByOrder(Order order);
}
