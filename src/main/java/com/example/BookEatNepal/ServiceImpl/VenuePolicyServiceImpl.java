package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Enums.PolicyStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.DTO.VenuePolicyDto;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Payload.Request.PolicyDeleteRequest;
import com.example.BookEatNepal.Payload.Request.PolicyUpdateRequest;
import com.example.BookEatNepal.Repository.VenuePolicyRepository;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Service.VenuePolicyService;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VenuePolicyServiceImpl implements VenuePolicyService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    VenuePolicyRepository venuePolicyRepository;
    @Autowired
    VenueRepo venueRepo;


    @Override
    public List<VenuePolicyDto> getVenuePolicyByVenueId(Integer venueId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<VenuePolicy> criteriaQuery = criteriaBuilder.createQuery(VenuePolicy.class);
        Root<VenuePolicy> root  = criteriaQuery.from(VenuePolicy.class);
        if(venueId == null) {
           throw new CustomException(CustomException.Type.VENUE_NOT_FOUND);
        }
        Predicate venueIdPredicate = criteriaBuilder.equal(root.get("venue").get("id"), venueId);
        criteriaQuery.where(venueIdPredicate);
        List<VenuePolicy> policyList = entityManager.createQuery(criteriaQuery).getResultList();
        return policyList.stream()
                .map(VenuePolicyDto::new)
                .toList();

    }

    @Override
    public List<VenuePolicyDto> getVenuePolicyByPolicyId(Integer policyId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<VenuePolicy> criteriaQuery = criteriaBuilder.createQuery(VenuePolicy.class);
        Root<VenuePolicy> root  = criteriaQuery.from(VenuePolicy.class);
        if(policyId == null) {
            throw new CustomException(CustomException.Type.VENUE_NOT_FOUND);
        }
        Predicate policyIdPrdicate = criteriaBuilder.equal(root.get("policyId"), policyId);
        criteriaQuery.where(policyIdPrdicate);
        List<VenuePolicy> policyList = entityManager.createQuery(criteriaQuery).getResultList();
        return policyList.stream()
                .map(VenuePolicyDto::new)
                .toList();
    }


    @Override
    public PolicyDeleteRequest deleteVenuePolicyByVenueId(Integer venueId) {
        VenuePolicy venuePolicy = venuePolicyRepository.findById(venueId).get();
        venuePolicyRepository.delete(venuePolicy);
        return new PolicyDeleteRequest(200,"Policy Deleted Successfully");
    }

    @Override
    public PolicyAddRequest addNewPolicy( VenuePolicy policy) {
        VenuePolicy venuePolicy = new VenuePolicy();
        venuePolicy.setCategory(policy.getCategory());
        venuePolicy.setPolicyName(policy.getPolicyName());
        venuePolicy.setDescription(policy.getDescription());
        venuePolicy.setCreatedAt(LocalDateTime.now());
        venuePolicy.setEffectiveDate(policy.getEffectiveDate());
        Venue venue =venueRepo.findById(policy.getVenue().getId()).orElseThrow(()-> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
        venuePolicy.setVenue(venue);
        venuePolicy.setStatus(PolicyStatus.INACTIVE);
        venuePolicyRepository.save(venuePolicy);
        return new PolicyAddRequest(201,"Policy Added Successfully",venuePolicy.getPolicyId());

    }

    @Override
    public PolicyAddRequest updateThePolicy(PolicyUpdateRequest policyUpdateRequest) {
       VenuePolicy venuePolicy = venuePolicyRepository.findById(policyUpdateRequest.getPolicyId()).orElseThrow();
       venuePolicy.setPolicyName(policyUpdateRequest.getPolicyName());
       venuePolicy.setStatus(policyUpdateRequest.getStatus());
       venuePolicy.setDescription(policyUpdateRequest.getDescription());
       venuePolicy.setCategory(policyUpdateRequest.getCategory());
       venuePolicy.setEffectiveDate(policyUpdateRequest.getEffectiveDate());
       venuePolicyRepository.save(venuePolicy);
        return new PolicyAddRequest(200,"Policy Updated SuccessFully",venuePolicy.getPolicyId());
    }
}
