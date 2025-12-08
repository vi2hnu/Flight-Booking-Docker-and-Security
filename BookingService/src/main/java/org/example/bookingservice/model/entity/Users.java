package org.example.bookingservice.model.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.example.bookingservice.model.enums.Gender;

import java.io.Serializable;

@Entity
@Data
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String name;

    String email;

    @Enumerated(EnumType.STRING)
    Gender gender;
}
