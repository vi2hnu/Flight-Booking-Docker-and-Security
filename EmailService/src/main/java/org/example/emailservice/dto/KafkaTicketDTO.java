package org.example.emailservice.dto;

import org.example.emailservice.model.entity.Users;

import java.util.List;

public record KafkaTicketDTO(
        Users user,
        ScheduleDTO scheduleDTO,
        ScheduleDTO returnTripDTO,
        List<PassengerDTO> passengers,
        String pnr
){}
