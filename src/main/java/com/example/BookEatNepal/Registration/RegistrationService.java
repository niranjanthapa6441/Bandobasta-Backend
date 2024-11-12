package com.example.BookEatNepal.Registration;

import com.example.BookEatNepal.Payload.Request.SignUpRequest;
import com.example.BookEatNepal.Service.AuthenticationService;
import com.example.BookEatNepal.Service.EmailService;
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
    private final EmailService emailService;
    public Status register(SignUpRequest registrationRequest){
        boolean isValidEmail = emailValidator.test(registrationRequest.getEmail());
        if (!isValidEmail){
            throw new IllegalStateException("invalid email");
        }
        String token=authenticationService.save(registrationRequest);
        String link="https://bandobasta.onrender.com/bandobasta/api/v1/user/authenticate/register/confirm?token="+token;
        emailService.sendEmail(registrationRequest.getEmail(),"Confirm your account Registration",buildEmail(
                registrationRequest.getFirstName(),
                link));
        return Status.REGISTERED;
    }
    @Transactional
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
        return Status.SUCCESS;
    }
    private String buildEmail(String name, String link) {
        String emailContent = "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">" +
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
                "<span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>" +
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
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Thank you for registering. Please click the link below to activate your account:</p>" +
                "<blockquote style=\"margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">" +
                "<p style=\"margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">" +
                "<a href=\"" + link + "\" style=\"color:#1D70B8;text-decoration:none;font-weight:700;\">Activate Now</a>" +
                "</p></blockquote>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">The link will expire in 15 minutes.</p>" +
                "<p style=\"font-size:16px;line-height:24px;color:#0b0c0c\">See you soon!</p>" +
                "</td>" +
                "<td width=\"10\" valign=\"middle\"><br></td>" +
                "</tr>" +
                "<tr>" +
                "<td height=\"30\"><br></td>" +
                "</tr>" +
                "</tbody></table>" +
                "</div>";

        return emailContent;
    }
}