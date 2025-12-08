package org.example.flightservice.model.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.example.flightservice.model.enums.Gender;

@Entity
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String name;

    String email;

    @Enumerated(EnumType.STRING)
    Gender gender;
}
