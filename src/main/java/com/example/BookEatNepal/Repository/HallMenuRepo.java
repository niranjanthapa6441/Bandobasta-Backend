package com.example.BookEatNepal.Repository;

import com.example.BookEatNepal.Model.HallMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallMenuRepo extends JpaRepository<HallMenu, Integer> {
}
