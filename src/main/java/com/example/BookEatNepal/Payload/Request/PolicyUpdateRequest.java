package com.example.BookEatNepal.Payload.Request;

import com.example.BookEatNepal.Enums.PolicyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyUpdateRequest {
    @NotNull(message = "Cannot be Null")
    private int policyId;
    private String category;
    private String policyName;
    private String description;
    private LocalDate effectiveDate;
    @Enumerated(EnumType.STRING)
    private PolicyStatus status;
}
