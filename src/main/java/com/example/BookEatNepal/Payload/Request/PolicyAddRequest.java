package com.example.BookEatNepal.Payload.Request;

import com.example.BookEatNepal.Enums.PolicyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyAddRequest {
    private int venueId;
    private String policyName;
    private String description;
    private String category;
    @Enumerated(EnumType.STRING)
    private PolicyStatus status;
    private LocalDateTime createdAt;
    private LocalDate effectiveDate;

}
