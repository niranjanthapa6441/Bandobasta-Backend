package com.example.BookEatNepal.ServiceImpl;
import com.example.BookEatNepal.Enums.PolicyStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.DTO.VenuePolicyDto;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Payload.Request.PolicyUpdateRequest;
import com.example.BookEatNepal.Repository.VenuePolicyRepository;
import com.example.BookEatNepal.Repository.VenueRepo;
import com.example.BookEatNepal.Service.VenuePolicyService;
import com.example.BookEatNepal.Util.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class VenuePolicyServiceImpl implements VenuePolicyService {
    private static final String SUCCESS_MESSAGE = "Successful";
    @Autowired
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
        if(venueId == null|| venueRepo.findById(venueId).isEmpty()) {
           throw new CustomException(CustomException.Type.VENUE_NOT_FOUND);
        }
        Predicate venueIdPredicate = criteriaBuilder.equal(root.get("venue").get("id"), venueId);
        criteriaQuery.where(venueIdPredicate);
        List<VenuePolicy> policyList = entityManager.createQuery(criteriaQuery).getResultList();
        return policyList.stream()
                .map(VenuePolicyDto::fromVenuePolicy)
                .collect(Collectors.toList());

    }

    @Override
    public List<VenuePolicyDto> getVenuePolicyByPolicyId(Integer policyId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<VenuePolicy> criteriaQuery = criteriaBuilder.createQuery(VenuePolicy.class);
        Root<VenuePolicy> root  = criteriaQuery.from(VenuePolicy.class);
        if(policyId == null||venuePolicyRepository.findById(policyId).isEmpty()) {
            throw new CustomException(CustomException.Type.POLICY_NOT_FOUND);
        }
        Predicate policyIdPrdicate = criteriaBuilder.equal(root.get("policyId"), policyId);
        criteriaQuery.where(policyIdPrdicate);
        List<VenuePolicy> policyList = entityManager.createQuery(criteriaQuery).getResultList();
        return policyList.stream()
                .map(VenuePolicyDto::fromVenuePolicy)
                .collect(Collectors.toList());
    }


    @Override
    public String deleteVenuePolicyByPolicyId(Integer venueId) {
        VenuePolicy venuePolicy = venuePolicyRepository.findById(venueId).get();
       venuePolicy.setStatus(PolicyStatus.INACTIVE);
       return SUCCESS_MESSAGE;
    }

    @Override
    public String addNewPolicy( PolicyAddRequest policy) {
        VenuePolicy venuePolicy = new VenuePolicy();
        venuePolicy.setCategory(policy.getCategory());
        venuePolicy.setPolicyName(policy.getPolicyName());
        venuePolicy.setDescription(policy.getDescription());
        venuePolicy.setCreatedAt(LocalDateTime.now());
        venuePolicy.setEffectiveDate(policy.getEffectiveDate());
        Venue venue =venueRepo.findById(policy.getVenueId()).orElseThrow(()-> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
        venuePolicy.setVenue(venue);
        venuePolicy.setStatus(PolicyStatus.INACTIVE);
        venuePolicyRepository.save(venuePolicy);
        return SUCCESS_MESSAGE;

    }

    @Override
    public String update(PolicyUpdateRequest policyUpdateRequest) {
       VenuePolicy venuePolicy = venuePolicyRepository.findById(policyUpdateRequest.getPolicyId()).get();
       venuePolicy.setPolicyName(policyUpdateRequest.getPolicyName());
       venuePolicy.setStatus(policyUpdateRequest.getStatus());
       venuePolicy.setDescription(policyUpdateRequest.getDescription());
       venuePolicy.setCategory(policyUpdateRequest.getCategory());
       venuePolicy.setEffectiveDate(policyUpdateRequest.getEffectiveDate());
       venuePolicyRepository.save(venuePolicy);
        return SUCCESS_MESSAGE;
    }
}
