package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Model.VenuePolicy;
import com.example.BookEatNepal.Service.VenuePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/venue")
public class VenuePolicyController {

    @Autowired
    VenuePolicyService policyService;


    @GetMapping("/policies")
    private List<VenuePolicy> getPolicyByVenue(@RequestParam Integer venueId)
    {
        return policyService.getVenuePolicyByVenueId(venueId);
    }
}
