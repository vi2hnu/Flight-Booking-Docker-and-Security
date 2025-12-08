package org.example.bookingservice.feign;

import org.example.bookingservice.dto.ScheduleDTO;
import org.example.bookingservice.dto.SeatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(name = "FlightService")
public interface FlightClient {
    @GetMapping("/api/schedule/{id}")
    ScheduleDTO getSchedule(@PathVariable Long id);

    @PostMapping("/api/schedule/check/seats/{id}")
    boolean checkSeats(@PathVariable("id") Long id, @RequestBody SeatsDTO seatsDTO);

    @PostMapping("/api/schedule/reserve/seats/{id}")
    boolean reserveSeats(@PathVariable("id") Long id, @RequestBody SeatsDTO seatsDTO);

    @DeleteMapping("/api/schedule/delete/seats/{id}")
    void deleteSeats(@PathVariable("id") Long id,@RequestBody SeatsDTO seatsDTO);
}
