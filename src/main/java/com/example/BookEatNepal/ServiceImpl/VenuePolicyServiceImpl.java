package com.example.BookEatNepal.ServiceImpl;
import com.example.BookEatNepal.Enums.PolicyStatus;
import com.example.BookEatNepal.Model.Venue;
import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.DTO.VenuePolicyDTO;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class VenuePolicyServiceImpl implements VenuePolicyService {
    private static final String SUCCESS_MESSAGE = "Successful";
    @Autowired
    EntityManager entityManager;
    @Autowired
    VenuePolicyRepository venuePolicyRepository;
    @Autowired
    VenueRepo venueRepo;

    // Method to convert a List of VenuePolicy to List of DTOs
    public static List<VenuePolicyDTO> toVenuePolicyDTOList(List<VenuePolicy> venuePolicies) {
        if (venuePolicies == null || venuePolicies.isEmpty()) {
            return Collections.emptyList();
        }
        List<VenuePolicyDTO> dtoList = new ArrayList<>();
        for (VenuePolicy venuePolicy : venuePolicies) {
            if (venuePolicy != null) {
                dtoList.add(toVenuePolicyDTO(venuePolicy));
            }
        }

        return dtoList;
    }

    public static VenuePolicyDTO toVenuePolicyDTO(VenuePolicy venuePolicy) {
        if (venuePolicy == null) {
            return null;
        }
        Venue venue = venuePolicy.getVenue();
        Integer venueId = (venue != null) ? venue.getId() : null;

        return VenuePolicyDTO.builder()
                .policyId(venuePolicy.getPolicyId())
                .category(venuePolicy.getCategory())
                .policyName(venuePolicy.getPolicyName())
                .description(venuePolicy.getDescription())
                .createdAt(venuePolicy.getCreatedAt())
                .effectiveDate(venuePolicy.getEffectiveDate())
                .status(venuePolicy.getStatus())
                .venueId(venueId)
                .build();
    }

    @Override
    public List<VenuePolicyDTO> getVenuePolicyByVenueId(Integer venueId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<VenuePolicy> criteriaQuery = criteriaBuilder.createQuery(VenuePolicy.class);
        Root<VenuePolicy> root = criteriaQuery.from(VenuePolicy.class);
        if (venueId == null || venueRepo.findById(venueId).isEmpty()) {
            throw new CustomException(CustomException.Type.VENUE_NOT_FOUND);
        }
        Predicate predicate = criteriaBuilder.equal(root.get("venue").get("id"), venueId);
        criteriaQuery.where(predicate);
        List<VenuePolicy> policyList = entityManager.createQuery(criteriaQuery).getResultList();
        return toVenuePolicyDTOList(policyList);
    }

    @Override
    public List<VenuePolicyDTO> getVenuePolicyByPolicyId(Integer policyId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<VenuePolicy> criteriaQuery = criteriaBuilder.createQuery(VenuePolicy.class);
        Root<VenuePolicy> root = criteriaQuery.from(VenuePolicy.class);
        if (policyId == null || venuePolicyRepository.findById(policyId).isEmpty()) {
            throw new CustomException(CustomException.Type.POLICY_NOT_FOUND);
        }
        Predicate predicate = criteriaBuilder.equal(root.get("policyId"), policyId);
        criteriaQuery.where(predicate);
        List<VenuePolicy> policyList = entityManager.createQuery(criteriaQuery).getResultList();
        return toVenuePolicyDTOList(policyList);
    }

    @Override
    public String delete(Integer venueId) {
        VenuePolicy venuePolicy = venuePolicyRepository.findById(venueId).get();
        venuePolicy.setStatus(PolicyStatus.INACTIVE);
        return SUCCESS_MESSAGE;
    }

    @Override
    public String save(PolicyAddRequest policy) {
        VenuePolicy venuePolicy = new VenuePolicy();
        venuePolicy.setCategory(policy.getCategory());
        venuePolicy.setPolicyName(policy.getPolicyName());
        venuePolicy.setDescription(policy.getDescription());
        venuePolicy.setCreatedAt(LocalDateTime.now());
        venuePolicy.setEffectiveDate(policy.getEffectiveDate());
        Venue venue = venueRepo.findById(policy.getVenueId())
                .orElseThrow(() -> new CustomException(CustomException.Type.VENUE_NOT_FOUND));
        venuePolicy.setVenue(venue);
        venuePolicy.setStatus(PolicyStatus.INACTIVE);
        venuePolicyRepository.save(venuePolicy);
        return SUCCESS_MESSAGE;

    }

    @Override
    public String update(PolicyUpdateRequest policyUpdateRequest) {
        int policyId = policyUpdateRequest.getPolicyId();
        Optional<VenuePolicy> venuePolicyOpt = venuePolicyRepository.findById(policyId);
        if (venuePolicyOpt.isEmpty()) {
            throw new CustomException(CustomException.Type.POLICY_NOT_FOUND);
        }
        VenuePolicy venuePolicy = venuePolicyOpt.get();
        venuePolicy.setPolicyName(policyUpdateRequest.getPolicyName());
        venuePolicy.setStatus(policyUpdateRequest.getStatus());
        venuePolicy.setDescription(policyUpdateRequest.getDescription());
        venuePolicy.setCategory(policyUpdateRequest.getCategory());
        venuePolicy.setEffectiveDate(policyUpdateRequest.getEffectiveDate());
        venuePolicyRepository.save(venuePolicy);
        return SUCCESS_MESSAGE;
    }


}


