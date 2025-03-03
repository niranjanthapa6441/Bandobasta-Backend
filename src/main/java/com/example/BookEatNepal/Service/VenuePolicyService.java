package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Payload.Request.PolicyUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface VenuePolicyService {
     List<VenuePolicy> getVenuePolicyByVenueId(Integer venueId);

     ResponseEntity<Optional<VenuePolicy>> getVenuePolicyByPolicyId(Integer policyId) ;

     String deleteVenuePolicyByVenueId(Integer venueId);

     PolicyAddRequest addNewPolicy(Integer venueId, String policyName, String description, String category, LocalDate effectiveDate);

     PolicyUpdateRequest updateThePolicy(Integer policyId);
}
