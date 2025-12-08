package org.example.bookingservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.example.bookingservice.model.entity.Passenger;
import org.example.bookingservice.model.enums.Gender;
import org.example.bookingservice.model.enums.Meal;

public record PassengerDTO(
        @NotBlank
        String name,

        @NotBlank
        Gender gender,

        @NotBlank
        Meal meal,

        @NotBlank
        String seatPos) {
    public PassengerDTO(Passenger passenger){
        this(
                passenger.getName(),
                passenger.getGender(),
                passenger.getMealOption(),
                passenger.getSeatPosition()
        );
    }
}
