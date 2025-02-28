package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventUserRepo extends JpaRepository<EventUser,Integer> {
}
