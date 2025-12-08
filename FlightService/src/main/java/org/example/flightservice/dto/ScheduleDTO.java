package org.example.flightservice.dto;

import jakarta.validation.constraints.NotNull;
import org.example.flightservice.model.entity.Schedule;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduleDTO (
        Long id,

        @NotNull
        Long flightId,

        @NotNull
        Long fromCityId,

        @NotNull
        Long toCityId,

        @NotNull
        LocalDate departureDate,

        @NotNull
        LocalDateTime departureTime,

        @NotNull
        Float price,

        int seatsAvailable,

        @NotNull
        Integer duration
) implements Serializable {
    public ScheduleDTO(Schedule s){
        this(
                s.getId(),
                s.getFlight() != null ? s.getFlight().getId() : null,
                s.getFromCity() != null ? s.getFromCity().getId() : null,
                s.getToCity() != null ? s.getToCity().getId() : null,
                s.getDepartureDate(),
                s.getDepartureTime(),
                s.getPrice(),
                s.getSeatsAvailable(),
                s.getDuration()
        );
    }

}

