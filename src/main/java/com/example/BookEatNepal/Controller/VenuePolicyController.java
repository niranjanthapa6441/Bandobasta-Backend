package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Payload.DTO.VenuePolicyDto;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Payload.Request.PolicyDeleteRequest;
import com.example.BookEatNepal.Payload.Request.PolicyUpdateRequest;
import com.example.BookEatNepal.Service.VenuePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/venue")
public class VenuePolicyController {

    @Autowired
    VenuePolicyService policyService;


    @GetMapping("/policies")
    private List<VenuePolicyDto> getPolicyByVenue(@RequestParam Integer venueId)
    {
        return policyService.getVenuePolicyByVenueId(venueId);
    }


    @GetMapping("/policy")
    private List<VenuePolicyDto> getPolicyByPolicyId(@RequestParam Integer policyId)
    {
        return policyService.getVenuePolicyByPolicyId(policyId);
    }



    @PostMapping(value = "/policies",consumes =  MediaType.APPLICATION_JSON_VALUE)
    private PolicyAddRequest addNewPolicy(@RequestBody VenuePolicy policy)
    {
    return policyService.addNewPolicy(policy);
    }


    @DeleteMapping("/delete/{id}")
    private PolicyDeleteRequest delete(@PathVariable Integer id)
    {
        return policyService.deleteVenuePolicyByVenueId(id);

    }

    @PutMapping("/update")
    private PolicyAddRequest updatePolicy(@RequestBody PolicyUpdateRequest updateRequest)
    {
        return policyService.updateThePolicy(updateRequest);
    }


}
