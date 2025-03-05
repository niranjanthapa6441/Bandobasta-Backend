package com.example.BookEatNepal.Payload.DTO;
import com.example.BookEatNepal.Enums.PolicyStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenuePolicy;
import lombok.*;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VenuePolicyDto {

    private int policyId;
    private String category;
    private String policyName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate effectiveDate;
    private PolicyStatus status;
    private Integer venueId;

    // Static method to convert a VenuePolicy entity into a VenuePolicyDto
    public static VenuePolicyDto fromVenuePolicy(VenuePolicy venuePolicy) {
        Venue venue = venuePolicy.getVenue();
        Integer venueId = (venue != null) ? venue.getId() : null;

        return VenuePolicyDto.builder()
                .policyId(venuePolicy.getPolicyId())
                .category(venuePolicy.getCategory())
                .policyName(venuePolicy.getPolicyName())
                .description(venuePolicy.getDescription())
                .createdAt(venuePolicy.getCreatedAt())
                .effectiveDate(venuePolicy.getEffectiveDate())
                .status(venuePolicy.getStatus())
                .venueId(venueId)
                .build();

    }
}
