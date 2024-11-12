package com.example.BookEatNepal.Service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
