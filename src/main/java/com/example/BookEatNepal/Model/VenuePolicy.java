package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.PolicyStatus;
import jakarta.persistence.*;
import lombok.*;
import org.joda.time.LocalDateTime;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "venue_policies")
public class VenuePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "category")
    private String category;

    @Column(name = "policy_name")
    private String policyName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PolicyStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @OneToOne
    @JoinColumn(name = "id")
    @Column(name = "venue_id")
    private Venue venue;
}
