package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Enums.PolicyStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
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
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public List<VenuePolicy> getVenuePolicyByVenueId(Integer venueId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<VenuePolicy> criteriaQuery = criteriaBuilder.createQuery(VenuePolicy.class);
        Root<VenuePolicy> root  = criteriaQuery.from(VenuePolicy.class);
        if(venueId != null) {
           throw new CustomException(CustomException.Type.VENUE_NOT_FOUND);
        }
        Predicate venueIdPredicate = criteriaBuilder.equal(root.get("venue").get("policyId"), venueId);
        criteriaQuery.where(venueIdPredicate);
        return entityManager.createQuery(criteriaQuery).getResultList();

    }

    @Override
    public ResponseEntity<Optional<VenuePolicy>> getVenuePolicyByPolicyId(Integer policyId) {
        if(policyId==null)
        {
            throw new CustomException(CustomException.Type.POLICY_NOT_FOUND);
        }
        Optional<VenuePolicy> policy = venuePolicyRepository.findById(policyId);
        return new ResponseEntity<>(policy, HttpStatus.OK);
    }


    @Override
    public String deleteVenuePolicyByVenueId(Integer venueId) {
        VenuePolicy venuePolicy = venuePolicyRepository.findById(venueId).get();
        venuePolicyRepository.delete(venuePolicy);
        return "Successfully Deleted the Policy";
    }

    @Override
    public PolicyAddRequest addNewPolicy( Integer venueId,String policyName, String description, String category, LocalDate effectiveDate) {
        VenuePolicy venuePolicy = new VenuePolicy();
        Venue venue =venueRepo.findById(venueId).get();
        venuePolicy.setCategory(category);
        venuePolicy.setPolicyName(policyName);
        venuePolicy.setDescription(description);
        venuePolicy.setCreatedAt(LocalDateTime.now());
        venuePolicy.setEffectiveDate(effectiveDate);
        venuePolicy.setVenue(venue);
        venuePolicy.setStatus(PolicyStatus.INACTIVE);
        venuePolicyRepository.save(venuePolicy);
        return new PolicyAddRequest(201,"Policy Added Successfully",venuePolicy.getPolicyId());

    }

    @Override
    public PolicyUpdateRequest updateThePolicy(Integer policyId) {
       VenuePolicy venuePolicy = venuePolicyRepository.findById(policyId).get();
 //Todo update logic

        return new PolicyUpdateRequest(venuePolicy.getCategory(),venuePolicy.getPolicyName(),venuePolicy.getDescription(),venuePolicy.getEffectiveDate(),venuePolicy.getStatus());
    }
}
