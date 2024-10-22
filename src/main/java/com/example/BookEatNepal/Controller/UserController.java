package com.example.BookEatNepal.Controller;

import com.example.BookEatNepal.Registration.RegistrationService;

import com.example.BookEatNepal.Request.LoginRequest;
import com.example.BookEatNepal.Request.SignUpRequest;
import com.example.BookEatNepal.Request.UpdateProfileRequest;
import com.example.BookEatNepal.Service.AuthenticationService;
import com.example.BookEatNepal.Util.RestResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AuthenticationService service;
    @Autowired
    private RegistrationService registrationService;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAll(){
        return RestResponse.ok(service.findAll());
    }
    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findById(@PathVariable String id){
        return RestResponse.ok(service.findById(Integer.valueOf(id)));
    }
    @PostMapping(value = "/authenticate/register",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(@Valid @RequestBody SignUpRequest request){
        return RestResponse.ok(registrationService.register(request));
    }
    @GetMapping(path = "/authenticate/register/confirm")
    public ResponseEntity<Object> confirm(@RequestParam("token") String token) {
        return RestResponse.ok(registrationService.confirmToken(token));
    }
    @PostMapping(value = "/authenticate/login",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request){
        return RestResponse.ok(service.login(request));
    }
    @PutMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable String id, @Valid @RequestBody UpdateProfileRequest request){

        return RestResponse.ok(service.update(Integer.valueOf(id),request));
    }
    @DeleteMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable String id){
        return RestResponse.ok(service.delete(Integer.valueOf(id)));
    }
    @PostMapping("/logOut")
    public ResponseEntity<?> logoutUser() {
        return RestResponse.ok(service.logout());
    }
}
