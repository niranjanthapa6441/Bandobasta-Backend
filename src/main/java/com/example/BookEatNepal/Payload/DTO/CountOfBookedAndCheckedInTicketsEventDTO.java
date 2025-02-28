package com.example.BookEatNepal.Payload.DTO;

import lombok.Data;

@Data
public class CountOfBookedAndCheckedInTicketsEventDTO {
    private int totalBooked;
    private int totalCheckedIn;
}
