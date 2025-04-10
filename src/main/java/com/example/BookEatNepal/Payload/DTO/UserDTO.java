package com.example.BookEatNepal.Payload.DTO;

import com.example.BookEatNepal.Enums.ERole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO{
    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private String username;
    private String dateOfBirth;
    private ERole role;
}
