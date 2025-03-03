package com.example.BookEatNepal.Payload.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyAddRequest {
    private Integer statusCode;
    private String message;
    private Integer policyId;
}
