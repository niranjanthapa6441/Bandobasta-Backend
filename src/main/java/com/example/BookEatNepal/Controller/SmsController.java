package com.example.BookEatNepal.Controller;
import com.example.BookEatNepal.ServiceImpl.SmsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {
    private final SmsServiceImpl smsService;

    public SmsController(SmsServiceImpl smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(
            @RequestParam String token,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String text) {
        String response = smsService.sendSms(token, from, to, text);
        return ResponseEntity.ok(response);
    }
}