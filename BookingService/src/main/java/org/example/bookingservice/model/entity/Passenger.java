package org.example.bookingservice.model.entity;

import lombok.Data;
import org.example.bookingservice.model.enums.Gender;
import org.example.bookingservice.model.enums.Meal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Data
@Entity
public class Passenger implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Enumerated(EnumType.STRING)
    Meal mealOption;

    String seatPosition;  //eg.12A

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonBackReference
    private Ticket ticket;

}
