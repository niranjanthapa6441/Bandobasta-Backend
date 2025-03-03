package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface VenuePolicyService {
    public List<VenuePolicy> getVenuePolicyByVenueId(Integer venueId);

    public VenuePolicy getVenuePolicyByPolicyId(Integer policyId);

    public String deleteVenuePolicyByVenueId(Integer venueId);

    public PolicyAddRequest addNewPolicy(Integer policyId, String policyName, String description, String category, LocalDate effectiveDate);

    public String updateThePolicy(Integer policyId);
}
