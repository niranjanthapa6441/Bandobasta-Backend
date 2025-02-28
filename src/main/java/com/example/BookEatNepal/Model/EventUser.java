    package com.example.BookEatNepal.Model;

    import jakarta.persistence.*;
    import lombok.Data;

    @Data
    @Entity
    @Table(name="event_user")
    public class EventUser {
        @Id
        @GeneratedValue(
                strategy = GenerationType.IDENTITY
        )
        @Column(name="id",length=10)
        private int id;

        @Column(name = "fullName", nullable = false)
        private String fullName;

        @Column(name = "email", nullable = false)
        private String email;

        @Column(name = "phone_number", nullable = false)
        private String phoneNumber;
    }
