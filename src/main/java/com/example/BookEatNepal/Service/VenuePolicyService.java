package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.DTO.VenuePolicyDto;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Payload.Request.PolicyDeleteRequest;
import com.example.BookEatNepal.Payload.Request.PolicyUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public interface VenuePolicyService {
     List<VenuePolicyDto> getVenuePolicyByVenueId(Integer venueId);

     List<VenuePolicyDto> getVenuePolicyByPolicyId(Integer policyId) ;

     PolicyDeleteRequest deleteVenuePolicyByVenueId(Integer venueId);

     PolicyAddRequest addNewPolicy(VenuePolicy policy);

     PolicyAddRequest updateThePolicy(PolicyUpdateRequest updateRequest);
}
