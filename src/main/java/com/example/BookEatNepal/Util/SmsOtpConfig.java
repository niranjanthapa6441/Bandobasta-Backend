package com.example.BookEatNepal.Util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SmsOtpConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}