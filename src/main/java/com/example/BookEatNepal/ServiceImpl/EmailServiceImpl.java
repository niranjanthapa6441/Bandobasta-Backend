package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String mail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(mail, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("bandobastanepal@gmail.com");

            mailSender.send(mimeMessage);
            logger.info("Email sent successfully to {}", to);

        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new IllegalStateException("Failed to send email", e);
        }
    }
}
