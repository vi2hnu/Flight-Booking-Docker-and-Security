package org.example.bookingservice.service.implmentation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservice.dto.*;
import org.example.bookingservice.exception.InvalidScheduleTimeException;
import org.example.bookingservice.exception.TicketNotFoundException;
import org.example.bookingservice.exception.UsersNotFoundException;
import org.example.bookingservice.feign.FlightClient;
import org.example.bookingservice.model.entity.Ticket;
import org.example.bookingservice.model.entity.Users;
import org.example.bookingservice.model.enums.Status;
import org.example.bookingservice.repository.*;
import org.example.bookingservice.service.TicketDetailsInterface;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TicketDetailsService implements TicketDetailsInterface {

    private final TicketRepository ticketRepository;
    private final UsersRepository usersRepository;
    private final PassengerRepository passengerRepository;
    private final FlightClient flightClient;
    private final KafkaTemplate<String, Object> kafka;

    public TicketDetailsService(TicketRepository ticketRepository, UsersRepository usersRepository,
                                PassengerRepository passengerRepository, FlightClient flightClient,
                                KafkaTemplate<String,Object> kafka) {
        this.ticketRepository = ticketRepository;
        this.usersRepository = usersRepository;
        this.passengerRepository = passengerRepository;
        this.flightClient = flightClient;
        this.kafka = kafka;
    }

    @Override
    public Ticket findTicketByPnr(String pnr) {
        Ticket ticket = ticketRepository.findTicketByPnr(pnr);

        //check if ticket is valid
        if(ticket == null){
            log.error("Invalid pnr number: {}",pnr);
            throw new TicketNotFoundException("Invalid pnr number");
        }
        return ticket;
    }

    @Override
    public List<TicketDTO> findHistoryByEmail(String email) {
        Users user = usersRepository.findByEmail(email);
        if(user==null){
            log.error("User not found: {}",email);
            throw new UsersNotFoundException("User Not Found");
        }
        List<Ticket> tickets =  ticketRepository.findAllByBookedByUsers_Id(user.getId());
        List<TicketDTO> result = new ArrayList<>();
        tickets.stream()
                .forEach(ticket ->{
                    ScheduleDTO schedule = flightClient.getSchedule(ticket.getScheduleId());
                    TicketDTO dto = new TicketDTO(
                            ticket.getPnr(),
                            ticket.getBookedByUsers(),
                            ticket.getPassengers(),
                            ticket.getStatus(),
                            schedule.fromCityId(),
                            schedule.toCityId(),
                            schedule.departureDate(),
                            schedule.departureTime());
                    result.add(dto);
                });
        return result;
    }

    @Override
    @Transactional
    public Ticket cancelTicket(String pnr) {
        Ticket ticket = ticketRepository.findTicketByPnr(pnr);
        LocalDateTime currentTime = LocalDateTime.now();

        if(ticket==null){
            log.error("Invalid pnr number: {}",pnr);
            throw new TicketNotFoundException("Invalid pnr number");
        }

        ScheduleDTO schedule = flightClient.getSchedule(ticket.getScheduleId());

        ScheduleDTO returnTrip = null;
        if(ticket.getReturnTripScheduleId()!=null){
            returnTrip = flightClient.getSchedule(ticket.getReturnTripScheduleId());
        }

        Duration diff = Duration.between(currentTime, schedule.departureTime()).abs();

        if (diff.toHours() < 24) {
            log.error("Cancellation window is 24 hours");
            throw new InvalidScheduleTimeException("Less than 24 hours gap");
        }

        if(ticket.getStatus()==Status.CANCELED){
            return ticket;
        }

        ticket.setStatus(Status.CANCELED);
        ticketRepository.save(ticket);

        List<String> seats = new ArrayList<>();

        ticket.getPassengers().forEach(passenger -> {
            //marking the booked seats as vacant
            seats.add(passenger.getSeatPosition());
            passengerRepository.delete(passenger);
        });

        List<PassengerDTO> passengers = new ArrayList<>();
        ticket.getPassengers().stream()
                        .forEach(passenger->passengers.add(new PassengerDTO(passenger)));

        kafka.send("ticket.cancelled",new KafkaTicketDTO(ticket.getBookedByUsers(),schedule,returnTrip,passengers,pnr));
        flightClient.deleteSeats(ticket.getScheduleId(),new SeatsDTO(seats));
        return ticket;
    }
}
