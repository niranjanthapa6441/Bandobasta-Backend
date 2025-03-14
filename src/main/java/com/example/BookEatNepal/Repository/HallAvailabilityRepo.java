package com.example.BookEatNepal.Repository;
import com.example.BookEatNepal.Enums.HallShift;
import com.example.BookEatNepal.Model.HallAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HallAvailabilityRepo extends JpaRepository<HallAvailability, Integer> {
    @Query("SELECT ha FROM HallAvailability ha WHERE ha.date = :date AND ha.shift = :shift")
    Optional<HallAvailability> findAvailableHallByDateAndShift(
            @Param("date") LocalDate date,
            @Param("shift") HallShift shift);
}

