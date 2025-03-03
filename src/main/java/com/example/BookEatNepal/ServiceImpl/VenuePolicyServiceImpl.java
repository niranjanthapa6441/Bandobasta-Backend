package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Repository.VenuePolicyRepository;
import com.example.BookEatNepal.Service.VenuePolicyService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VenuePolicyServiceImpl implements VenuePolicyService {
    @Autowired
    EntityManager entityManager;
    @Autowired
    VenuePolicyRepository venuePolicyRepository;


    @Override
    public List<VenuePolicy> getVenuePolicyByVenueId(Integer venueId) {
        return List.of();
    }

    @Override
    public VenuePolicy getVenuePolicyByPolicyId(Integer policyId) {
        return null;
    }

    @Override
    public String deleteVenuePolicyByVenueId(Integer venueId) {
        return "";
    }

    @Override
    public PolicyAddRequest addNewPolicy(Integer policyId, String policyName, String description, String category, LocalDate effectiveDate) {
        return null;
    }

    @Override
    public String updateThePolicy(Integer policyId) {
        return "";
    }
}
