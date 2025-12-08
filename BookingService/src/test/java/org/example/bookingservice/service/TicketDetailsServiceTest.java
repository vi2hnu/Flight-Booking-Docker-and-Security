package org.example.bookingservice.service;

import org.example.bookingservice.dto.KafkaTicketDTO;
import org.example.bookingservice.dto.ScheduleDTO;
import org.example.bookingservice.exception.InvalidScheduleTimeException;
import org.example.bookingservice.exception.TicketNotFoundException;
import org.example.bookingservice.exception.UsersNotFoundException;
import org.example.bookingservice.model.entity.Passenger;
import org.example.bookingservice.model.entity.Ticket;
import org.example.bookingservice.model.entity.Users;
import org.example.bookingservice.model.enums.Gender;
import org.example.bookingservice.model.enums.Meal;
import org.example.bookingservice.model.enums.Status;
import org.example.bookingservice.repository.PassengerRepository;
import org.example.bookingservice.repository.TicketRepository;
import org.example.bookingservice.repository.UsersRepository;
import org.example.bookingservice.service.implmentation.TicketDetailsService;
import org.example.bookingservice.feign.FlightClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketDetailsServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private FlightClient flightClient;

    @Mock
    private KafkaTemplate<String,Object> kafka;

    @InjectMocks
    private TicketDetailsService ticketDetailsService;

    @Test
    void findTicketByPnr_ThrowsTicketNotFound() {
        when(ticketRepository.findTicketByPnr("PNR1")).thenReturn(null);
        assertThrows(TicketNotFoundException.class, () -> ticketDetailsService.findTicketByPnr("PNR1"));
    }

    @Test
    void findTicketByPnr_Success() {
        Ticket ticket = new Ticket();
        when(ticketRepository.findTicketByPnr("PNR1")).thenReturn(ticket);
        ticketDetailsService.findTicketByPnr("PNR1");
        verify(ticketRepository).findTicketByPnr("PNR1");
    }

    @Test
    void findHistoryByEmail_ThrowsUsersNotFound() {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(null);
        assertThrows(UsersNotFoundException.class, () -> ticketDetailsService.findHistoryByEmail("test@example.com"));
    }

    @Test
    void findHistoryByEmail_Success() {
        Users user = new Users();
        user.setId(1L);
        when(usersRepository.findByEmail("test@example.com")).thenReturn(user);
        when(ticketRepository.findAllByBookedByUsers_Id(1L)).thenReturn(new ArrayList<>());
        ticketDetailsService.findHistoryByEmail("test@example.com");
        verify(usersRepository).findByEmail("test@example.com");
        verify(ticketRepository).findAllByBookedByUsers_Id(1L);
    }

    @Test
    void cancelTicket_ThrowsTicketNotFound() {
        when(ticketRepository.findTicketByPnr("PNR1")).thenReturn(null);
        assertThrows(TicketNotFoundException.class, () -> ticketDetailsService.cancelTicket("PNR1"));
    }

    @Test
    void cancelTicket_ThrowsInvalidScheduleTimeException() {
        Ticket ticket = new Ticket();
        ticket.setScheduleId(1L);

        ScheduleDTO scheduleDTO = new ScheduleDTO(1L, 1L, 1L, 2L, LocalDate.now(),
                LocalDateTime.now().plusHours(1), 100F, 100, 120);

        when(ticketRepository.findTicketByPnr("PNR1")).thenReturn(ticket);
        when(flightClient.getSchedule(1L)).thenReturn(scheduleDTO);

        assertThrows(InvalidScheduleTimeException.class, () -> ticketDetailsService.cancelTicket("PNR1"));
    }

    @Test
    void cancelTicket_Success() {
        Ticket ticket = new Ticket();
        ticket.setScheduleId(1L);
        ticket.setStatus(Status.BOOKED);
        ticket.setPassengers(new ArrayList<>());

        ScheduleDTO scheduleDTO = new ScheduleDTO(1L, 1L, 1L, 2L, LocalDate.now(),
                LocalDateTime.now().plusDays(2), 100F, 100, 120);

        when(ticketRepository.findTicketByPnr("PNR1")).thenReturn(ticket);
        when(flightClient.getSchedule(1L)).thenReturn(scheduleDTO);

        ticketDetailsService.cancelTicket("PNR1");

        verify(ticketRepository).save(ticket);
        verify(kafka).send(anyString(), any(KafkaTicketDTO.class));
    }

    @Test
    void cancelTicket_AlreadyCancelled() {
        Ticket ticket = new Ticket();
        ticket.setScheduleId(1L);
        ticket.setStatus(Status.CANCELED);
        ticket.setPassengers(new ArrayList<>());

        ScheduleDTO scheduleDTO = new ScheduleDTO(1L, 1L, 1L, 2L,
                LocalDate.now(),
                LocalDateTime.now().plusDays(2),
                100F, 100, 120);

        when(ticketRepository.findTicketByPnr("PNR1")).thenReturn(ticket);
        when(flightClient.getSchedule(1L)).thenReturn(scheduleDTO);

        Ticket result = ticketDetailsService.cancelTicket("PNR1");

        verify(ticketRepository, never()).save(ticket);
        verify(kafka, never()).send(anyString(), any());

        assertEquals(Status.CANCELED, result.getStatus());
    }

    @Test
    void cancelTicketWithPassengers_Success() {
        Ticket ticket = new Ticket();
        ticket.setScheduleId(1L);
        ticket.setStatus(Status.BOOKED);

        Passenger passenger1 = new Passenger();
        passenger1.setSeatPosition("A1");
        passenger1.setName("Passenger 1");
        passenger1.setGender(Gender.FEMALE);
        passenger1.setMealOption(Meal.VEG);
        passenger1.setTicket(ticket);

        Passenger passenger2 = new Passenger();
        passenger2.setSeatPosition("B1");
        passenger2.setName("Passenger 2");
        passenger2.setGender(Gender.MALE);
        passenger2.setMealOption(Meal.NONVEG);
        passenger2.setTicket(ticket);

        ticket.setPassengers(new ArrayList<>(List.of(passenger1, passenger2)));
        ScheduleDTO scheduleDTO = new ScheduleDTO(1L, 1L, 1L, 2L, LocalDate.now(),
                LocalDateTime.now().plusDays(2), 100F, 100, 120);

        when(ticketRepository.findTicketByPnr("PNR1")).thenReturn(ticket);
        when(flightClient.getSchedule(1L)).thenReturn(scheduleDTO);

        ticketDetailsService.cancelTicket("PNR1");


        verify(ticketRepository).save(ticket);
        verify(kafka).send(anyString(), any(KafkaTicketDTO.class));
        verify(passengerRepository).delete(passenger1);
        verify(passengerRepository).delete(passenger2);
    }


}

