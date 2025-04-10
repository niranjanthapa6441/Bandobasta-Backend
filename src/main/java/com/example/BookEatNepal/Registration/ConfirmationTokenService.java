package com.example.BookEatNepal.Registration;

import com.example.BookEatNepal.Repository.ConfirmTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmTokenRepository confirmTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token){
        confirmTokenRepository.save(token);
    }
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmTokenRepository.findByToken(token);
    }
    public boolean isTokenExists(String token) {
        return confirmTokenRepository.findByToken(token).isPresent();
    }
    public int setConfirmedAt(String token) {
        return confirmTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
    public void deleteToken(ConfirmationToken token){
        confirmTokenRepository.delete(token);
    }
}
