package org.example.flightservice.dto;

import org.example.flightservice.model.entity.Schedule;

import java.util.List;

public record SearchDTO(
        Schedule schedule,
        List<String> bookedSeats
) {
}
