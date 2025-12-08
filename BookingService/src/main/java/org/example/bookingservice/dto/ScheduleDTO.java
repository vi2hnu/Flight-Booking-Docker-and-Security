package org.example.bookingservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduleDTO(
        Long id,
        Long flightId,
        Long fromCityId,
        Long toCityId,
        LocalDate departureDate,
        LocalDateTime departureTime,
        float price,
        int seatsAvailable,
        int duration
) {}