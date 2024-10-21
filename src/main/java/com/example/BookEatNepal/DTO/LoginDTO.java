package com.example.BookEatNepal.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDTO {
    private String username;
    private String id;
    private String type;
    private String accessToken;
}
