package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.DTO.LoginDTO;
import com.example.BookEatNepal.DTO.UserDTO;
import com.example.BookEatNepal.Enums.ERole;
import com.example.BookEatNepal.Model.AppUser;
import com.example.BookEatNepal.Model.Role;
import com.example.BookEatNepal.Registration.ConfirmationTokenService;
import com.example.BookEatNepal.Registration.MessageResponse;
import com.example.BookEatNepal.Registration.ConfirmationToken;
import com.example.BookEatNepal.Repository.AppUserRepo;
import com.example.BookEatNepal.Repository.RoleRepo;
import com.example.BookEatNepal.Request.LoginRequest;
import com.example.BookEatNepal.Request.SignUpRequest;
import com.example.BookEatNepal.Request.UpdateProfileRequest;
import com.example.BookEatNepal.Security.JWT.JwtUtils;
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
    JwtUtils jwtUtils;

    public AuthenticationServiceImpl(ConfirmationTokenService confirmationTokenService) {
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override
    public String save(SignUpRequest request) {
        checkValidation(request);
        AppUser user = toUser(request);
        AppUser saveUser = repo.save(user);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                saveUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }
    @Override
    public int enableAppUser(String email) {
        Optional<AppUser> findUser=repo.findByEmail(email);
        AppUser updateUser=new AppUser();
        if (findUser.isPresent()){
            AppUser appUser= findUser.get();
            updateUser.setId(appUser.getId());
            updateUser.setFirstName(appUser.getFirstName());
            updateUser.setEmail(appUser.getEmail());
            updateUser.setLastName(appUser.getLastName());
            updateUser.setMiddleName(appUser.getMiddleName());
            updateUser.setPhoneNumber(appUser.getPhoneNumber());
            updateUser.setUsername(appUser.getUsername());
            updateUser.setPassword(appUser.getPassword());
            updateUser.setEnabled(true);
            updateUser.setLocked(true);
            updateUser.setRole(appUser.getRole());
            updateUser.setStatus("Registered");
            repo.save(updateUser);
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
        Optional<AppUser> findUser = repo.findById(id);
        checkUpdateValidation(request,findUser.get());
        if (findUser.isPresent()) {
            AppUser updateUser = toupdateUser(request,findUser.get());
            AppUser updatedUser = repo.save(updateUser);
            return "User Updated Successfully";
        } else
            throw new NullPointerException("The User does not exist");
    }

    @Override
    public String delete(int id) {
        Optional<AppUser> findUser = repo.findById(id);
        if (findUser.isPresent()) {
            AppUser deleteUser = findUser.get();
            deleteUser.setStatus("terminated");
            AppUser deletedUser = repo.save(deleteUser);
            return "user deleted";
        } else
            throw new NullPointerException("The User Doesn't Exist");
    }

    @Override
    public UserDTO findById(int id) {
        Optional<AppUser> findUser = repo.findById(id);
        if (findUser.isPresent()) {
            AppUser user = findUser.get();
            return toUserDTO(user);
        } else
            throw new NullPointerException("The User Doesn't Exist");
    }
    @Override
    public Iterable<AppUser> findAll() {
        return repo.findAll();
    }
    private UserDTO toUserDTO(AppUser user) {
        return UserDTO.builder().
                id(user.getId()).
                email(user.getEmail()).
                firstName(user.getFirstName()).
                lastName(user.getLastName()).
                middleName(user.getMiddleName()).
                phoneNumber(user.getPhoneNumber()).
                build();
    }

    private AppUser toUser(SignUpRequest request) {
        AppUser user=new AppUser();
        user.setFirstName(request.getFirstName());
        user.setEmail(request.getEmail());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(getRole(request.getRole()));
        user.setStatus("Registered");
        return user;
    }
    private AppUser toupdateUser(UpdateProfileRequest request,AppUser user) {
        AppUser updateUser=new AppUser();
        updateUser.setId(user.getId());
        updateUser.setFirstName(request.getFirstName());
        updateUser.setEmail(request.getEmail());
        updateUser.setLastName(request.getLastName());
        updateUser.setMiddleName(request.getMiddleName());
        updateUser.setPhoneNumber(request.getPhoneNumber());
        updateUser.setUsername(user.getUsername());
        updateUser.setPassword(user.getPassword());
        updateUser.setRole(user.getRole());
        updateUser.setStatus("Registered");
        return updateUser;
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
        Optional<AppUser> User=repo.findByEmail(request.getEmail());
        if (User.isPresent()){
            if (User.get().getId() == user.getId())
                throw new CustomException(CustomException.Type.EMAIL_ALREADY_EXISTS);
        }

    }
    private void checkUpdatePhoneNumber(UpdateProfileRequest request,AppUser user) {
        Optional<AppUser> User=repo.findByPhoneNumber(request.getPhoneNumber());
        if (User.isPresent()){
            if (User.get().getId()== user.getId())
                throw new CustomException(CustomException.Type.PHONE_NUMBER_ALREADY_EXISTS);
        }
    }
    private void checkEmail(SignUpRequest request) {
        Optional<AppUser> User=repo.findByEmail(request.getEmail());
        if (User.isPresent())
            throw new CustomException(CustomException.Type.EMAIL_ALREADY_EXISTS);
    }
    private void checkUsername(SignUpRequest request) {
        Optional<AppUser> User=repo.findByUsername(request.getUsername());
        if (User.isPresent())
            throw new CustomException(CustomException.Type.USERNAME_ALREADY_EXISTS);
    }
    private void checkPhoneNumber(SignUpRequest request) {
        Optional<AppUser> User=repo.findByPhoneNumber(request.getPhoneNumber());
        if (User.isPresent())
            throw new CustomException(CustomException.Type.PHONE_NUMBER_ALREADY_EXISTS);
    }
}
