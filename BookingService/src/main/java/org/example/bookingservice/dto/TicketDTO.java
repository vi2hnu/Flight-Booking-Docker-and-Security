package org.example.bookingservice.dto;

import org.example.bookingservice.model.entity.Passenger;
import org.example.bookingservice.model.entity.Users;
import org.example.bookingservice.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TicketDTO(
        String pnr,
        Users bookedByUsers,
        List<Passenger> passengers,
        Status status,
        Long fromCityId,
        Long toCityId,
        LocalDate departureDate,
        LocalDateTime departureTime
) {
}
