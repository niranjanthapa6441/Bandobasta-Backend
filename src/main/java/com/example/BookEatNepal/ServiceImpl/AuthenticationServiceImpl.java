package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.LoginDTO;
import com.example.BookEatNepal.DTO.UserDTO;
import com.example.BookEatNepal.Enums.ERole;
import com.example.BookEatNepal.Model.AppUser;
import com.example.BookEatNepal.Model.Role;
import com.example.BookEatNepal.Registration.ConfirmationTokenService;
import com.example.BookEatNepal.Registration.MessageResponse;
import com.example.BookEatNepal.Registration.RegistrationToken.ConfirmationToken;
import com.example.BookEatNepal.Repository.AppUserRepo;
import com.example.BookEatNepal.Repository.RoleRepo;
import com.example.BookEatNepal.Request.LoginRequest;
import com.example.BookEatNepal.Request.SignUpRequest;
import com.example.BookEatNepal.Request.UpdateProfileRequest;
import com.example.BookEatNepal.Security.JWT.JWTUtils;
import com.example.BookEatNepal.Service.AuthenticationService;
import com.example.BookEatNepal.Util.CustomException;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private AppUserRepo repo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private RoleRepo roleRepository;

    @Autowired
    private  ConfirmationTokenService confirmationTokenService;


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTUtils jwtUtils;

    public AuthenticationServiceImpl(ConfirmationTokenService confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override
    public String save(SignUpRequest request) {
        checkValidation(request);
        AppUser user = toCustomer(request);
        AppUser saveCustomer = repo.save(user);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                saveCustomer
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }
    @Override
    public int enableAppUser(String email) {
        Optional<AppUser> findCustomer=repo.findByEmail(email);
        AppUser updateCustomer=new AppUser();
        if (findCustomer.isPresent()){
            AppUser appUser= findCustomer.get();
            updateCustomer.setId(appUser.getId());
            updateCustomer.setFirstName(appUser.getFirstName());
            updateCustomer.setEmail(appUser.getEmail());
            updateCustomer.setLastName(appUser.getLastName());
            updateCustomer.setMiddleName(appUser.getMiddleName());
            updateCustomer.setPhoneNumber(appUser.getPhoneNumber());
            updateCustomer.setUsername(appUser.getUsername());
            updateCustomer.setPassword(appUser.getPassword());
            updateCustomer.setEnabled(true);
            updateCustomer.setLocked(true);
            updateCustomer.setRole(appUser.getRole());
            updateCustomer.setStatus("Registered");
            repo.save(updateCustomer);
        }
        return 1;
    }
    @Override
    public ResponseEntity<MessageResponse> logout() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @Override
    public LoginDTO login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return LoginDTO.builder()
                .id(String.valueOf(userDetails.getId()))
                .username(userDetails.getUsername())
                .type("Bearer")
                .accessToken(jwt)
                .build();
    }
    @Override
    public String update(int id, UpdateProfileRequest request) {
        Optional<AppUser> findCustomer = repo.findById(id);
        checkUpdateValidation(request,findCustomer.get());
        if (findCustomer.isPresent()) {
            AppUser updateCustomer = toUpdateCustomer(request,findCustomer.get());
            AppUser updatedCustomer = repo.save(updateCustomer);
            return "User Updated Successfully";
        } else
            throw new NullPointerException("The customer does not exist");
    }

    @Override
    public String delete(int id) {
        Optional<AppUser> findCustomer = repo.findById(id);
        if (findCustomer.isPresent()) {
            AppUser deleteCustomer = findCustomer.get();
            deleteCustomer.setStatus("terminated");
            AppUser deletedCustomer = repo.save(deleteCustomer);
            return "user deleted";
        } else
            throw new NullPointerException("The Customer Doesn't Exist");
    }

    @Override
    public UserDTO findById(int id) {
        Optional<AppUser> findCustomer = repo.findById(id);
        if (findCustomer.isPresent()) {
            AppUser customer = findCustomer.get();
            return toCustomerDTO(customer);
        } else
            throw new NullPointerException("The Customer Doesn't Exist");
    }
    @Override
    public Iterable<AppUser> findAll() {
        return repo.findAll();
    }
    private UserDTO toCustomerDTO(AppUser customer) {
        return UserDTO.builder().
                id(customer.getId()).
                email(customer.getEmail()).
                firstName(customer.getFirstName()).
                lastName(customer.getLastName()).
                middleName(customer.getMiddleName()).
                phoneNumber(customer.getPhoneNumber()).
                build();
    }

    private AppUser toCustomer(SignUpRequest request) {
        AppUser customer=new AppUser();
        customer.setFirstName(request.getFirstName());
        customer.setEmail(request.getEmail());
        customer.setLastName(request.getLastName());
        customer.setMiddleName(request.getMiddleName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setUsername(request.getUsername());
        customer.setPassword(encoder.encode(request.getPassword()));
        customer.setRole(getRole(request.getRole()));
        customer.setStatus("Registered");
        return customer;
    }
    private AppUser toUpdateCustomer(UpdateProfileRequest request,AppUser customer) {
        AppUser updateCustomer=new AppUser();
        updateCustomer.setId(customer.getId());
        updateCustomer.setFirstName(request.getFirstName());
        updateCustomer.setEmail(request.getEmail());
        updateCustomer.setLastName(request.getLastName());
        updateCustomer.setMiddleName(request.getMiddleName());
        updateCustomer.setPhoneNumber(request.getPhoneNumber());
        updateCustomer.setUsername(customer.getUsername());
        updateCustomer.setPassword(customer.getPassword());
        updateCustomer.setRole(customer.getRole());
        updateCustomer.setStatus("Registered");
        return updateCustomer;
    }


    private Role getRole(String role) {
        Role getRole=roleRepository.findByName(ERole.valueOf(role));
        return getRole;
    }

    private void checkValidation(SignUpRequest request) {
        checkEmail(request);
        checkUsername(request);
        checkPhoneNumber(request);
    }
    private void checkUpdateValidation(UpdateProfileRequest request,AppUser user) {
        checkUpdateEmail(request,user);
        checkUpdatePhoneNumber(request,user);
    }
    private void checkUpdateEmail(UpdateProfileRequest request,AppUser user) {
        Optional<AppUser> customer=repo.findByEmail(request.getEmail());
        if (customer.isPresent()){
            if (customer.get().getId() == user.getId())
                throw new CustomException(CustomException.Type.EMAIL_ALREADY_EXISTS);
        }

    }
    private void checkUpdatePhoneNumber(UpdateProfileRequest request,AppUser user) {
        Optional<AppUser> customer=repo.findByPhoneNumber(request.getPhoneNumber());
        if (customer.isPresent()){
            if (customer.get().getId()== user.getId())
                throw new CustomException(CustomException.Type.PHONE_NUMBER_ALREADY_EXISTS);
        }
    }
    private void checkEmail(SignUpRequest request) {
        Optional<AppUser> customer=repo.findByEmail(request.getEmail());
        if (customer.isPresent())
            throw new CustomException(CustomException.Type.EMAIL_ALREADY_EXISTS);
    }
    private void checkUsername(SignUpRequest request) {
        Optional<AppUser> customer=repo.findByUsername(request.getUsername());
        if (customer.isPresent())
            throw new CustomException(CustomException.Type.USERNAME_ALREADY_EXISTS);
    }
    private void checkPhoneNumber(SignUpRequest request) {
        Optional<AppUser> customer=repo.findByPhoneNumber(request.getPhoneNumber());
        if (customer.isPresent())
            throw new CustomException(CustomException.Type.PHONE_NUMBER_ALREADY_EXISTS);
    }
}
