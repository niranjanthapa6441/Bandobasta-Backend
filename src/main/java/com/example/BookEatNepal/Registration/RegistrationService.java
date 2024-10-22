package com.example.BookEatNepal.Registration;

import com.example.BookEatNepal.Request.SignUpRequest;
import com.example.BookEatNepal.Service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final AuthenticationService authenticationService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(SignUpRequest registrationRequest){
        boolean isValidEmail = emailValidator.test(registrationRequest.getEmail());
        if (!isValidEmail){
            throw new IllegalStateException("invalid email");
        }
     String token=authenticationService.save(registrationRequest);
        return token;
    }
    public Status confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        authenticationService.enableAppUser(
                confirmationToken.getUser().getEmail());
        return Status.REGISTERED;
    }
}
