package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<AppUser> findByPhoneNumber(String phoneNumber);
}
