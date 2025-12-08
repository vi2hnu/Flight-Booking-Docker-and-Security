package org.example.bookingservice.service;

import org.example.bookingservice.dto.*;
import org.example.bookingservice.exception.InvalidScheduleTimeException;
import org.example.bookingservice.exception.SeatNotAvailableException;
import org.example.bookingservice.exception.UsersNotFoundException;
import org.example.bookingservice.feign.FlightClient;
import org.example.bookingservice.model.entity.Ticket;
import org.example.bookingservice.model.entity.Users;
import org.example.bookingservice.model.enums.Gender;
import org.example.bookingservice.model.enums.Meal;
import org.example.bookingservice.model.enums.Status;
import org.example.bookingservice.repository.TicketRepository;
import org.example.bookingservice.repository.UsersRepository;
import org.example.bookingservice.service.implmentation.TicketBookingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketBookingServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private FlightClient flightClient;

    @Mock
    private KafkaTemplate<String, KafkaTicketDTO> kafka;

    @InjectMocks
    private TicketBookingService ticketBookingService;

    @Test
    void ticketBookingTest_ThrowsInvalidScheduleTime() {
        Users user = new Users();
        user.setId(1L);
        TicketBookingDTO dto = new TicketBookingDTO(user, null, null, List.of());

        when(usersRepository.getUsersById(1L)).thenReturn(user);
        when(flightClient.getSchedule(null)).thenReturn(null);

        assertThrows(InvalidScheduleTimeException.class, () -> ticketBookingService.getTicket(dto));
    }

    @Test
    void ticketBookingTest_SeatNotAvailable() {
        Users user = new Users();
        user.setId(1L);

        ScheduleDTO outbound = new ScheduleDTO(1L, 1L, 2L, 3L, LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2), 500f, 100, 120);
        PassengerDTO passenger = new PassengerDTO("Alice", Gender.FEMALE, Meal.VEG, "A1");
        TicketBookingDTO dto = new TicketBookingDTO(user, 1L, null, List.of(passenger));

        when(usersRepository.getUsersById(1L)).thenReturn(user);
        when(flightClient.getSchedule(1L)).thenReturn(outbound);
        when(flightClient.reserveSeats(eq(1L), any(SeatsDTO.class))).thenReturn(false);

        assertThrows(SeatNotAvailableException.class, () -> ticketBookingService.getTicket(dto));
    }

    @Test
    void ticketBookingTest_SeatNotEnough() {
        Users user = new Users();
        user.setId(1L);

        ScheduleDTO outbound = new ScheduleDTO(1L, 1L, 2L, 3L, LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2), 500f, 1, 120);
        PassengerDTO p1 = new PassengerDTO("Alice", Gender.FEMALE, Meal.VEG, "A1");
        PassengerDTO p2 = new PassengerDTO("Bob", Gender.MALE, Meal.NONVEG, "A2");
        TicketBookingDTO dto = new TicketBookingDTO(user, 1L, null, List.of(p1, p2));

        when(usersRepository.getUsersById(1L)).thenReturn(user);
        when(flightClient.getSchedule(1L)).thenReturn(outbound);

        assertThrows(SeatNotAvailableException.class, () -> ticketBookingService.getTicket(dto));
    }

    @Test
    void ticketBookingTest_Success() {
        Users user = new Users();
        user.setId(1L);

        ScheduleDTO outbound = new ScheduleDTO(1L, 1L, 2L, 3L, LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2), 500f, 100, 120);
        PassengerDTO passenger = new PassengerDTO("Alice", Gender.FEMALE, Meal.VEG, "A1");
        TicketBookingDTO dto = new TicketBookingDTO(user, 1L, null, List.of(passenger));

        when(usersRepository.getUsersById(1L)).thenReturn(user);
        when(flightClient.getSchedule(1L)).thenReturn(outbound);
        when(flightClient.reserveSeats(eq(1L), any(SeatsDTO.class))).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = ticketBookingService.getTicket(dto);

        assertNotNull(result.getPnr());
        assertEquals(Status.BOOKED, result.getStatus());
        assertEquals(1, result.getPassengers().size());
        verify(kafka).send(eq("ticket.booked"), any(KafkaTicketDTO.class));
    }

    @Test
    void ticketBookingTestWithReturnId_Success() {
        Users user = new Users();
        user.setId(1L);

        ScheduleDTO outbound = new ScheduleDTO(1L, 1L, 2L, 3L, LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2), 500f, 100, 120);
        ScheduleDTO returnTrip = new ScheduleDTO(2L, 1L, 2L, 3L, LocalDate.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 500f, 100, 120);
        PassengerDTO passenger = new PassengerDTO("Alice", Gender.FEMALE, Meal.VEG, "A1");
        TicketBookingDTO dto = new TicketBookingDTO(user, 1L, 2L, List.of(passenger));

        lenient().when(usersRepository.getUsersById(1L)).thenReturn(user);
        lenient().when(flightClient.getSchedule(1L)).thenReturn(outbound);
        lenient().when(flightClient.getSchedule(2L)).thenReturn(returnTrip);
        lenient().when(flightClient.reserveSeats(eq(1L), any(SeatsDTO.class))).thenReturn(true);
        lenient().when(flightClient.reserveSeats(eq(2L), any(SeatsDTO.class))).thenReturn(true);
        lenient().when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = ticketBookingService.getTicket(dto);

        assertNotNull(result.getPnr());
        assertEquals(Status.BOOKED, result.getStatus());
        assertEquals(1, result.getPassengers().size());
        verify(kafka).send(eq("ticket.booked"), any(KafkaTicketDTO.class));
    }
}
