package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.ServiceImpl.SmsServiceImpl;
import com.example.BookEatNepal.Util.RestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms/otp")
public class SmsController {
    private final SmsServiceImpl smsService;

    public SmsController(SmsServiceImpl smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<Object> sendSms(
            @RequestParam String token,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String text) {
        return RestResponse.ok(smsService.sendSms(token, from, to, text));

    }
}