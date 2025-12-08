package org.example.flightservice.service;

import org.example.flightservice.dto.ScheduleDTO;
import org.example.flightservice.model.entity.Schedule;

public interface AirLineInterface {
    public Schedule addSchedule(ScheduleDTO schedule);
}
