package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;

@Data
@Entity
@Table(name="event")
public class Event {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @Column(name="title", nullable = false, length = 30)
    private String title;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(nullable = false, name = "date")
    private LocalDate date;

    @Column(nullable = false,name = "start_time")
    private Time startTime;

    @Column(nullable = false,name = "end_time")
    private Time endTime;
}
