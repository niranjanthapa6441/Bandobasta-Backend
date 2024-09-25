package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo  extends JpaRepository<Payment, Integer> {
}
