package com.example.BookEatNepal.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="hall_image")
public class HallImage{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10)
    private int id;

    @Column(name = "image_url", nullable = false, unique = true)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;
}
