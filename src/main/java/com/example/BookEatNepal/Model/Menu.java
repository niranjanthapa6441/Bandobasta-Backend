package com.example.BookEatNepal.Model;

import com.example.BookEatNepal.Enums.MenuStatus;
import com.example.BookEatNepal.Enums.MenuType;
import com.example.BookEatNepal.Enums.PackageType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="menu")
public class Menu {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name="id",length=10)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
    @Lob
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name="menu_type",length = 20, nullable = false)
    private MenuType menuType;

    @Enumerated(EnumType.STRING)
    @Column(name="status",length = 20, nullable = false)
    private MenuStatus status;
}
