package org.example.bookingservice.service.implmentation;

import lombok.extern.slf4j.Slf4j;
import org.example.bookingservice.dto.*;
import org.example.bookingservice.exception.InvalidScheduleTimeException;
import org.example.bookingservice.exception.SeatNotAvailableException;
import org.example.bookingservice.exception.UsersNotFoundException;
import org.example.bookingservice.feign.FlightClient;
import org.example.bookingservice.model.entity.*;
import org.example.bookingservice.model.enums.Status;
import org.example.bookingservice.repository.TicketRepository;
import org.example.bookingservice.repository.UsersRepository;
import org.example.bookingservice.service.TicketBookingInterface;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TicketBookingService implements TicketBookingInterface {

    private final TicketRepository ticketRepository;
    private final FlightClient flightClient;
    private final KafkaTemplate<String, KafkaTicketDTO> kafka;
    private final UsersRepository usersRepository;

    public TicketBookingService(TicketRepository ticketRepository, FlightClient flightClient
                                , KafkaTemplate<String, KafkaTicketDTO> kafka, UsersRepository usersRepository) {
        this.ticketRepository = ticketRepository;
        this.flightClient = flightClient;
        this.kafka = kafka;
        this.usersRepository = usersRepository;
    }

    @Override
    public Ticket getTicket(TicketBookingDTO ticketDTO) {
        Users user = usersRepository.getUsersById(ticketDTO.user().getId());

        if(user==null){
            throw new UsersNotFoundException("User not found");
        }

        ScheduleDTO outbound = flightClient.getSchedule(ticketDTO.scheduleId());

        if (outbound == null) {
            log.error("Invalid schedule: {}",ticketDTO.scheduleId());
            throw new InvalidScheduleTimeException("Invalid schedule");
        }

        ScheduleDTO returnTrip = null;
        if (ticketDTO.returnTripId() != null) {
            returnTrip = flightClient.getSchedule(ticketDTO.returnTripId());
        }

        List<String> seats = ticketDTO.passengers().stream()
                .map(PassengerDTO::seatPos)
                .toList();
        SeatsDTO seatsDTO = new SeatsDTO(seats);

        if (outbound.seatsAvailable() < ticketDTO.passengers().size()) {
            log.error("Not enough seats available");
            throw new SeatNotAvailableException("Not enough seats available");
        }
        
        if(!flightClient.reserveSeats(outbound.id(), seatsDTO)){
            throw new SeatNotAvailableException("Seats already booked");
        }

        Ticket saved = createTicket(ticketDTO, outbound, returnTrip);
        saved.setBookedByUsers(user);

        kafka.send("ticket.booked",new KafkaTicketDTO(ticketDTO.user(),outbound,returnTrip,ticketDTO.passengers(),
                                            saved.getPnr()));
        savePassengers(saved, ticketDTO);

        return saved;
    }

    private Ticket createTicket(TicketBookingDTO dto, ScheduleDTO outbound, ScheduleDTO returnTrip) {

        Ticket ticket = new Ticket();
        String pnr = "PNR" + System.currentTimeMillis() + dto.user().getId();

        ticket.setPnr(pnr);

        ticket.setScheduleId(outbound.id());

        if (returnTrip != null) {
            ticket.setReturnTripScheduleId(returnTrip.id());
        }

        ticket.setStatus(Status.BOOKED);

        return ticketRepository.save(ticket);
    }

    private void savePassengers(Ticket ticket, TicketBookingDTO dto) {
        List<Passenger> passengers = new ArrayList<>();

        dto.passengers().forEach(p -> {
            Passenger passenger = new Passenger();
            passenger.setName(p.name());
            passenger.setGender(p.gender());
            passenger.setMealOption(p.meal());
            passenger.setSeatPosition(p.seatPos());
            passenger.setTicket(ticket);
            passengers.add(passenger);
        });

        ticket.setPassengers(passengers);
        ticketRepository.save(ticket);
    }
}
