package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Enums.ERole;
import com.example.BookEatNepal.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role,Integer> {
    Role findByName(ERole name);
}
