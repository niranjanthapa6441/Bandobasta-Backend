package com.example.BookEatNepal.Service;

import com.example.BookEatNepal.DTO.LoginDTO;
import com.example.BookEatNepal.DTO.UserDTO;
import com.example.BookEatNepal.Model.AppUser;
import com.example.BookEatNepal.Registration.MessageResponse;
import com.example.BookEatNepal.Request.LoginRequest;
import com.example.BookEatNepal.Request.SignUpRequest;
import com.example.BookEatNepal.Request.UpdateProfileRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {
    String save(SignUpRequest request);
    Iterable<AppUser> findAll();
    String update(int id, UpdateProfileRequest request);
    String delete(int id);
    UserDTO findById(int id);
    LoginDTO login(LoginRequest request);
    public int enableAppUser(String email);

    public ResponseEntity<MessageResponse> logout();
}
