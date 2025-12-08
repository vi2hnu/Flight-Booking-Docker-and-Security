package org.example.flightservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.flightservice.dto.ScheduleDTO;
import org.example.flightservice.dto.SeatsDTO;
import org.example.flightservice.service.ScheduleInterface;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleInterface scheduleService;

    public ScheduleController(ScheduleInterface scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{id}")
    public ScheduleDTO getSchedule(@PathVariable Long id) {
        return scheduleService.getSchedule(id);
    }

    @PostMapping("/check/seats/{id}")
    public boolean checkSeats(@PathVariable Long id, @RequestBody SeatsDTO seatsDTO) {
        log.info("seats at controller {}", seatsDTO);
        return scheduleService.checkSeats(id, seatsDTO);
    }

    @PostMapping("/reserve/seats/{id}")
    public boolean reserveSeats(@PathVariable Long id, @RequestBody SeatsDTO seatsDTO) {
        return scheduleService.reserveSeats(id, seatsDTO);
    }

    @DeleteMapping("/delete/seats/{id}")
    void deleteSeats(@PathVariable("id") Long id, @RequestBody SeatsDTO seatsDTO) {
        scheduleService.deleteSeats(id,seatsDTO);
    }

}
