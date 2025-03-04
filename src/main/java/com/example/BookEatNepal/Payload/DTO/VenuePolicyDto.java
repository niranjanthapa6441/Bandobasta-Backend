package com.example.BookEatNepal.Payload.DTO;

import com.example.BookEatNepal.Enums.PolicyStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenuePolicy;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data

public class VenuePolicyDto {

    private Integer policyId;
    private String category;
    private String policyName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate effectiveDate;
    private PolicyStatus status;

    private Integer venueId;

    public VenuePolicyDto(VenuePolicy venuePolicy) {
        this.policyId = venuePolicy.getPolicyId();
        this.category = venuePolicy.getCategory();
        this.policyName = venuePolicy.getPolicyName();
        this.description = venuePolicy.getDescription();
        this.createdAt = venuePolicy.getCreatedAt();
        this.effectiveDate = venuePolicy.getEffectiveDate();
        this.status = venuePolicy.getStatus();
       //Extracting Venue Id
        Venue venue = venuePolicy.getVenue();
        this.venueId = (venue != null) ? venue.getId() : null;
    }

}