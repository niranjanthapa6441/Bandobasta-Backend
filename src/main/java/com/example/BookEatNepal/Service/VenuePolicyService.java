package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.DTO.VenuePolicyDto;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Payload.Request.PolicyUpdateRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface VenuePolicyService {
     List<VenuePolicyDto> getVenuePolicyByVenueId(Integer venueId);

     List<VenuePolicyDto> getVenuePolicyByPolicyId(Integer policyId) ;

     String deleteVenuePolicyByPolicyId(Integer venueId);

     String addNewPolicy(PolicyAddRequest policy);

     String updateThePolicy(PolicyUpdateRequest updateRequest);
}
