package com.example.BookEatNepal.Payload.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "The username or email should not be empty")
    private String username;
    @NotBlank(message = "The password should not be empty")
    private String password;
}
