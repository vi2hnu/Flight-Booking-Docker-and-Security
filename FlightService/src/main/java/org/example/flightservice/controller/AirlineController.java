package org.example.flightservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.flightservice.dto.ScheduleDTO;
import org.example.flightservice.model.entity.Schedule;
import org.example.flightservice.service.AirLineInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/flight/airline")
public class AirlineController {

    private final AirLineInterface airLineInterface;

    public AirlineController(AirLineInterface airLineInterface) {
        this.airLineInterface = airLineInterface;
    }

    @PostMapping("/inventory")
    public ResponseEntity<Schedule> addSchedule(@Valid @RequestBody ScheduleDTO schedule) {
        return ResponseEntity.status(HttpStatus.CREATED).body(airLineInterface.addSchedule(schedule));
    }

}
