package org.example.flightservice.service;

import org.example.flightservice.dto.SeatsDTO;
import org.example.flightservice.exception.ScheduleNotFoundException;
import org.example.flightservice.model.entity.Schedule;
import org.example.flightservice.repository.BookedSeatsRepository;
import org.example.flightservice.repository.ScheduleRepository;
import org.example.flightservice.service.implementation.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private BookedSeatsRepository bookedSeatsRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    void getSchedule_ThrowsScheduleNotFound() {
        when(scheduleRepository.findScheduleById(1L)).thenReturn(null);
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.getSchedule(1L));
    }

    @Test
    void getSchedule_Success() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        when(scheduleRepository.findScheduleById(1L)).thenReturn(schedule);
        scheduleService.getSchedule(1L);
        verify(scheduleRepository, times(1)).findScheduleById(1L);
    }

    @Test
    void checkSeats_CallsRepository() {
        List<String> seats = List.of("1A");
        SeatsDTO seatsDTO = new SeatsDTO(seats);
        scheduleService.checkSeats(1L, seatsDTO);
        verify(bookedSeatsRepository, times(1)).existsBySchedule_IdAndSeatPos(1L, "1A");
    }

    @Test
    void reserveSeats_ThrowsScheduleNotFound() {
        SeatsDTO seatsDTO = new SeatsDTO(List.of("1A"));
        when(scheduleRepository.findScheduleById(1L)).thenReturn(null);
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.reserveSeats(1L, seatsDTO));
    }

    @Test
    void reserveSeats_Success() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        SeatsDTO seatsDTO = new SeatsDTO(List.of("1A"));

        when(scheduleRepository.findScheduleById(1L)).thenReturn(schedule);
        scheduleService.reserveSeats(1L, seatsDTO);

        verify(bookedSeatsRepository, times(1)).save(any());
    }

    @Test
    void deleteSeats_Success() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        SeatsDTO seatsDTO = new SeatsDTO(List.of("1A"));

        when(scheduleRepository.findScheduleById(1L)).thenReturn(schedule);
        scheduleService.deleteSeats(1L, seatsDTO);

        verify(bookedSeatsRepository, times(1)).deleteBySchedule_IdAndSeatPos(1L, "1A");
    }

    @Test
    void addSeats_ThrowsScheduleNotFound() {
        when(scheduleRepository.findScheduleById(1L)).thenReturn(null);
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.addSeats(1L, 5));
    }

    @Test
    void addSeats_Success() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setSeatsAvailable(10);

        when(scheduleRepository.findScheduleById(1L)).thenReturn(schedule);
        scheduleService.addSeats(1L, 5);

        verify(scheduleRepository, times(1)).save(schedule);
    }
}
