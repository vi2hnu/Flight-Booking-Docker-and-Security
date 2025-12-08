package org.example.flightservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.example.flightservice.model.enums.Gender;
import org.example.flightservice.model.enums.Meal;

public record PassengerDTO(
        @NotBlank
        String name,

        @NotBlank
        Gender gender,

        @NotBlank
        Meal meal,

        @NotBlank
        String seatPos) {}
