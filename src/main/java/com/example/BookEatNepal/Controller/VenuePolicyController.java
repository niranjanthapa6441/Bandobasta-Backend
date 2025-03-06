package com.example.BookEatNepal.Controller;
import com.example.BookEatNepal.Payload.Request.PolicyAddRequest;
import com.example.BookEatNepal.Payload.Request.PolicyUpdateRequest;
import com.example.BookEatNepal.Service.VenuePolicyService;
import com.example.BookEatNepal.Util.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/venue")
public class VenuePolicyController {

    @Autowired
    VenuePolicyService policyService;


    @GetMapping("/policies")
    private ResponseEntity<Object> getPolicyByVenueId(@RequestParam Integer venueId)
    {
        return RestResponse.ok(policyService.getVenuePolicyByVenueId(venueId),"Data retrieval Successful");
    }


    @GetMapping("/policy")
    private ResponseEntity<Object> getPolicyByPolicyId(@RequestParam Integer policyId)
    {
        return RestResponse.ok(policyService.getVenuePolicyByPolicyId(policyId),"Data Retrieval Successful");
    }


    @PostMapping(value = "/policy/save",consumes =  MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<Object> addNewPolicy(@RequestBody PolicyAddRequest policy)
    {
    return RestResponse.ok(policyService.save(policy));
    }


    @DeleteMapping("/policy/{id}")
    private ResponseEntity<Object> delete(@PathVariable Integer id)
    {

        return RestResponse.ok( policyService.delete(id));

    }


    @PostMapping("/policy/update")
    private ResponseEntity<Object> updatePolicy(@RequestBody PolicyUpdateRequest updateRequest)
    {
        return RestResponse.ok( policyService.update(updateRequest));
    }

}
