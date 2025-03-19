package com.example.BookEatNepal.Model;
import com.example.BookEatNepal.Enums.HallShift;
import com.example.BookEatNepal.Enums.HallStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Time;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "hall_availability")
public class HallAvailability {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name = "id", length = 10)
    private int id;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false, name = "date")
    private LocalDate date;

    @Column(nullable = false, name = "start_time")
    private Time startTime;

    @Column(nullable = false, name = "end_time")
    private Time endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift", length = 20, nullable = true)
    private HallShift shift;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private HallStatus status;
}