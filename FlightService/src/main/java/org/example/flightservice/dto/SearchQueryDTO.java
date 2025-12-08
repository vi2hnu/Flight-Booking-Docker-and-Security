package org.example.flightservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

public record SearchQueryDTO(
        @NotBlank
        String fromCityCode,

        @NotBlank
        String toCityCode,

        @NotNull
        LocalDate date) implements Serializable {
}
