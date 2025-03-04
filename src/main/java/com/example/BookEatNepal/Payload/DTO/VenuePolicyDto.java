package com.example.BookEatNepal.Payload.DTO;
import com.example.BookEatNepal.Enums.PolicyStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenuePolicy;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VenuePolicyDto {

    private int policyId;
    private String category;
    private String policyName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate effectiveDate;
    private PolicyStatus status;
    private Integer venueId;

    // Private constructor to be used by the Builder
    private VenuePolicyDto(Builder builder) {
        this.policyId = builder.policyId;
        this.category = builder.category;
        this.policyName = builder.policyName;
        this.description = builder.description;
        this.createdAt = builder.createdAt;
        this.effectiveDate = builder.effectiveDate;
        this.status = builder.status;
        this.venueId = builder.venueId;
    }

    // Static Builder class
    public static class Builder {
        private int policyId;
        private String category;
        private String policyName;
        private String description;
        private LocalDateTime createdAt;
        private LocalDate effectiveDate;
        private PolicyStatus status;
        private Integer venueId;

        // Setters in the builder
        public Builder setPolicyId(Integer policyId) {
            this.policyId = policyId;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setPolicyName(String policyName) {
            this.policyName = policyName;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setEffectiveDate(LocalDate effectiveDate) {
            this.effectiveDate = effectiveDate;
            return this;
        }

        public Builder setStatus(PolicyStatus status) {
            this.status = status;
            return this;
        }

        public Builder setVenueId(Integer venueId) {
            this.venueId = venueId;
            return this;
        }

        public VenuePolicyDto build() {
            return new VenuePolicyDto(this);
        }
    }


    public static VenuePolicyDto fromVenuePolicy(VenuePolicy venuePolicy) {
        Venue venue = venuePolicy.getVenue();
        Integer venueId = (venue != null) ? venue.getId() : null;

        return new Builder()
                .setPolicyId(venuePolicy.getPolicyId())
                .setCategory(venuePolicy.getCategory())
                .setPolicyName(venuePolicy.getPolicyName())
                .setDescription(venuePolicy.getDescription())
                .setCreatedAt(venuePolicy.getCreatedAt())
                .setEffectiveDate(venuePolicy.getEffectiveDate())
                .setStatus(venuePolicy.getStatus())
                .setVenueId(venueId)
                .build();
    }
}
