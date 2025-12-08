package org.example.bookingservice.dto;

import org.example.bookingservice.model.entity.Users;

import java.util.List;

public record KafkaTicketDTO(
        Users user,
        ScheduleDTO scheduleDTO,
        ScheduleDTO returnTripDTO,
        List<PassengerDTO> passengers,
        String pnr
){}
