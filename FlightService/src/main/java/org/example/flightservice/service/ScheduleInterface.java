package org.example.flightservice.service;

import org.example.flightservice.dto.ScheduleDTO;
import org.example.flightservice.dto.SeatsDTO;


public interface ScheduleInterface {
    ScheduleDTO getSchedule(Long scheduleId);
    boolean checkSeats(Long scheduleId, SeatsDTO seatsDTO);
    boolean reserveSeats(Long scheduleId, SeatsDTO seatsDTO);
    void deleteSeats(Long scheduleId, SeatsDTO seatsDTO);
    void addSeats(Long scheduleId, int seats);
}
