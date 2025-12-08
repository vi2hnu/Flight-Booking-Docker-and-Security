package org.example.bookingservice.repository;

import org.example.bookingservice.model.entity.Ticket;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
    Ticket findTicketByPnr(String pnr);
    List<Ticket> findAllByBookedByUsers_Id(Long id);
}
