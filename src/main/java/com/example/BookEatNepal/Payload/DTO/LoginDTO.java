package com.example.BookEatNepal.Payload.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginDTO {
    private String username;
    private String id;
    private String type;
    private String accessToken;
    private List<String> roles;
}
