package org.example.bookingservice.service;

import org.example.bookingservice.dto.TicketBookingDTO;
import org.example.bookingservice.model.entity.Ticket;

public interface TicketBookingInterface {
    public Ticket getTicket(TicketBookingDTO ticketBookingDTO);
}
