package org.example.bookingservice.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservice.dto.TicketBookingDTO;
import org.example.bookingservice.model.entity.Ticket;
import org.example.bookingservice.service.TicketBookingInterface;
import org.example.bookingservice.service.TicketDetailsInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/flight/booking/")
public class BookingController {

    private final TicketBookingInterface ticketBookingInterface;
    private final TicketDetailsInterface ticketDetailsInterface;

    @Autowired
    public BookingController(TicketBookingInterface ticketBookingInterface, TicketDetailsInterface ticketDetailsInterface) {
        this.ticketBookingInterface = ticketBookingInterface;
        this.ticketDetailsInterface = ticketDetailsInterface;
    }

    @PostMapping("{flightId}")
    public ResponseEntity<Ticket> bookFlight(@PathVariable Long flightId, @Valid @RequestBody TicketBookingDTO ticketBookingDTO){
        TicketBookingDTO ticket = new TicketBookingDTO(ticketBookingDTO.user(), ticketBookingDTO.scheduleId(),
                        ticketBookingDTO.returnTripId(), ticketBookingDTO.passengers());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketBookingInterface.getTicket(ticket));
    }

    @GetMapping("history/{emailId:.+}")
    public ResponseEntity<List<Ticket>> findHistoryByEmail(@PathVariable String emailId) {
        return ResponseEntity.status(HttpStatus.OK).body(ticketDetailsInterface.findHistoryByEmail(emailId));
    }

    @DeleteMapping("cancel/{pnr}")
    public ResponseEntity<Void> cancelTicket(@PathVariable String pnr) {
        ticketDetailsInterface.cancelTicket(pnr);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
