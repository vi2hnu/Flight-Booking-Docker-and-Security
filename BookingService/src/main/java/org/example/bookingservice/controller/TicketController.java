package org.example.bookingservice.controller;

import org.example.bookingservice.model.entity.Ticket;
import org.example.bookingservice.service.TicketDetailsInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flight/ticket/")
public class TicketController {

    private final TicketDetailsInterface ticketDetailsInterface;

    public TicketController(TicketDetailsInterface ticketDetailsInterface) {
        this.ticketDetailsInterface = ticketDetailsInterface;
    }

    @GetMapping("/{pnr}")
    public ResponseEntity<Ticket> findTicketByPnr(@PathVariable String pnr) {
        return ResponseEntity.status(HttpStatus.OK).body(ticketDetailsInterface.findTicketByPnr(pnr));
    }


}
