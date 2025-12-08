package org.example.bookingservice.controller;


import org.example.bookingservice.model.entity.Ticket;
import org.example.bookingservice.service.TicketDetailsInterface;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketDetailsInterface ticketDetailsInterface;

    @InjectMocks
    private TicketController ticketController;

    @Test
    void findTicketByPnr_success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();

        Ticket ticket = new Ticket();
        ticket.setPnr("PNR12345");

        when(ticketDetailsInterface.findTicketByPnr("PNR12345")).thenReturn(ticket);

        mockMvc.perform(get("/api/flight/ticket/PNR12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "pnr":"PNR12345"
                        }
                        """));

        verify(ticketDetailsInterface).findTicketByPnr("PNR12345");
    }
}
