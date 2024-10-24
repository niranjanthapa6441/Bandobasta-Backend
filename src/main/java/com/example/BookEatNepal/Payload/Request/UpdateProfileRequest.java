package com.example.BookEatNepal.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "First Name should not be empty")
    private String firstName;
    @NotBlank(message = "last Name should not be empty")
    private String lastName;
    private String middleName;
    @Email
    @NotBlank(message = "Email should not be empty")
    private String email;
    @NotBlank(message = "Email should not be empty")
    private String phoneNumber;
}
