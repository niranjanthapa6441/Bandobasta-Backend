package com.example.BookEatNepal.ServiceImpl;

import com.example.BookEatNepal.Payload.DTO.LoginDTO;
import com.example.BookEatNepal.Payload.DTO.UserDTO;
import com.example.BookEatNepal.Enums.ERole;
import com.example.BookEatNepal.Model.AppUser;
import com.example.BookEatNepal.Model.Role;
import com.example.BookEatNepal.Payload.Request.RequestPasswordRequest;
import com.example.BookEatNepal.Registration.ConfirmationTokenService;
import com.example.BookEatNepal.Registration.MessageResponse;
import com.example.BookEatNepal.Registration.ConfirmationToken;
import com.example.BookEatNepal.Repository.AppUserRepo;
import com.example.BookEatNepal.Repository.RoleRepo;
import com.example.BookEatNepal.Payload.Request.LoginRequest;
import com.example.BookEatNepal.Payload.Request.SignUpRequest;
import com.example.BookEatNepal.Payload.Request.UpdateProfileRequest;
import com.example.BookEatNepal.Security.JWT.JwtUtils;
import com.example.BookEatNepal.Service.AuthenticationService;
import com.example.BookEatNepal.Service.EmailService;
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

import java.security.SecureRandom;
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
    private EmailService emailService;

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

        SecureRandom random = new SecureRandom();
        String token;
        do {
            int otp = 100000 + random.nextInt(900000);
            token = String.valueOf(otp);
        } while (confirmationTokenService.isTokenExists(token));

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
    public String sendForgetPasswordEmail(String email) {
        AppUser user = repo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " not found"));

        SecureRandom random = new SecureRandom();
        String token;
        do {
            int otp = 100000 + random.nextInt(900000);
            token = String.valueOf(otp);
        } while (confirmationTokenService.isTokenExists(token));

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String subject = "Password Reset Request";
        String body = buildEmail(user.getFirstName(),token );

        emailService.sendEmail(email, subject, body);

        return "Password reset OTP sent successfully to " + email;
    }

    @Override
    public String verifyOTP(String otp) {
        Optional<ConfirmationToken> optionalToken = confirmationTokenService.getToken(otp);

        if (optionalToken.isEmpty()) {
            throw new CustomException(CustomException.Type.INVALID_OTP);
        }

        ConfirmationToken token = optionalToken.get();

        if (token.getConfirmedAt() != null) {
            throw new CustomException(CustomException.Type.OTP_ALREADY_USED);
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(CustomException.Type.OTP_HAS_EXPIRED);
        }

        token.setConfirmedAt(LocalDateTime.now());
        confirmationTokenService.saveConfirmationToken(token);

        return "OTP verified successfully";
    }

    @Override
    public String resetUserPassword(RequestPasswordRequest request) {
        Optional<AppUser> findUser=repo.findByEmail(request.getEmail());
        Optional<ConfirmationToken> optionalToken = confirmationTokenService.getToken(request.getOtp());

        if (optionalToken.isEmpty()) {
            throw new CustomException(CustomException.Type.INVALID_OTP);
        }

        ConfirmationToken token = optionalToken.get();

        if (token.getConfirmedAt() != null) {
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
                updateUser.setPassword(encoder.encode(request.getPassword()));
                updateUser.setEnabled(true);
                updateUser.setLocked(true);
                updateUser.setRole(appUser.getRole());
                updateUser.setStatus(appUser.getStatus());
                repo.save(updateUser);
            }
            confirmationTokenService.deleteToken(token);
        }
        else {
            throw new CustomException(CustomException.Type.OTP_HAS_NOT_BEEN_VERIFIED);
        }

        return "password updated successfully";
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
                .roles(roles)
                .accessToken(jwt)
                .build();
    }
    @Override
    public String update(int id, UpdateProfileRequest request) {
        Optional<AppUser> findUser = repo.findById(id);
        checkUpdateValidation(request,findUser.get());
        if (findUser.isPresent()) {
            AppUser updateUser = toUpdateUser(request,findUser.get());
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
        user.setEnabled(false);
        user.setLocked(true);
        user.setStatus("Registered");
        return user;
    }
    private AppUser toUpdateUser(UpdateProfileRequest request,AppUser user) {
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
        updateUser.setEnabled(true);
        updateUser.setLocked(true);
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
    private String buildEmail(String name, String otp) {

        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
                "<tbody><tr>" +
                "<td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">" +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">" +
                "<tbody><tr>" +
                "<td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">" +
                "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td style=\"padding-left:10px\"></td>" +
                "<td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">" +
                "<span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Your OTP Code</span>" +
                "</td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "</td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td width=\"10\" height=\"10\" valign=\"middle\"></td>" +
                "<td>" +
                "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">" +
                "<tbody><tr>" +
                "<td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>" +
                "</tr></tbody></table>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\" height=\"10\"></td>" +
                "</tr>" +
                "</tbody></table>" +
                "<table role=\"presentation\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">" +
                "<tbody><tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "<td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p>" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Your OTP for verification is:</p>" +
                "<blockquote style=\"margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">" +
                "<strong style=\"font-size:24px;color:#1D70B8\">" + otp + "</strong>" +
                "</p></blockquote>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">The OTP is valid for 15 minutes.</p>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">If you did not request this, please ignore this email.</p>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">Thank you!</p>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">Regards,</p>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\"><strong>Bandobasta Team</strong></p>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "</tbody></table>" +
                "</div>";
    }

}
