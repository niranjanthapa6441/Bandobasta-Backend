package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.TicketPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPaymentRepo extends JpaRepository<TicketPayment,Integer> {
}
