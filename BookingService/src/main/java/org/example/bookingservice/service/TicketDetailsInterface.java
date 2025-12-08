package org.example.bookingservice.service;

import java.util.List;

import org.example.bookingservice.model.entity.Ticket;

public interface TicketDetailsInterface {
    Ticket findTicketByPnr(String pnr);
    List<Ticket> findHistoryByEmail(String email);
    Ticket cancelTicket(String pnr);
}
