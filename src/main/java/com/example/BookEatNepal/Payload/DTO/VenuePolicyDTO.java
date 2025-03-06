package com.example.BookEatNepal.Payload.DTO;


import com.example.BookEatNepal.Enums.PolicyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VenuePolicyDTO {

    private int policyId;
    private String category;
    private String policyName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate effectiveDate;
    private PolicyStatus status;
    private Integer venueId;


}
