package org.example.emailservice.dto;

import org.example.emailservice.model.enums.Gender;
import org.example.emailservice.model.enums.Meal;

public record PassengerDTO(
        String name,
        Gender gender,
        Meal meal,
        String seatPos) {}
