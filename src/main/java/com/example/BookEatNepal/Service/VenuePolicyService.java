package com.example.BookEatNepal.Service;
import com.example.BookEatNepal.Payload.DTO.VenuePolicyDTO;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Payload.Request.PolicyUpdateRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface VenuePolicyService {
    List<VenuePolicyDTO> getVenuePolicyByVenueId(Integer venueId);

    List<VenuePolicyDTO> getVenuePolicyByPolicyId(Integer policyId);

    String delete(Integer venueId);

    String save(PolicyAddRequest policy);

    String update(PolicyUpdateRequest updateRequest);
}
