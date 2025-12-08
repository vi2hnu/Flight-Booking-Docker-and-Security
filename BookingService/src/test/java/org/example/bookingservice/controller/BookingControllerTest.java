package org.example.bookingservice.controller;

import org.example.bookingservice.dto.PassengerDTO;
import org.example.bookingservice.dto.TicketBookingDTO;
import org.example.bookingservice.model.entity.Ticket;
import org.example.bookingservice.model.entity.Users;
import org.example.bookingservice.model.enums.Gender;
import org.example.bookingservice.model.enums.Meal;
import org.example.bookingservice.service.TicketBookingInterface;
import org.example.bookingservice.service.TicketDetailsInterface;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private TicketBookingInterface ticketBookingInterface;

    @Mock
    private TicketDetailsInterface ticketDetailsInterface;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void bookFlight_success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        Users user = new Users();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setGender(Gender.MALE);

        Ticket ticket = new Ticket();
        ticket.setPnr("PNR123");

        when(ticketBookingInterface.getTicket(org.mockito.ArgumentMatchers.any(TicketBookingDTO.class))).thenReturn(ticket);

        String jsonBody = """
                {
                  "user": {
                    "id": 1,
                    "name": "John",
                    "email": "john@example.com",
                    "gender": "MALE"
                  },
                  "scheduleId": 1,
                  "returnTripId": null,
                  "passengers": [
                    {
                      "name": "Alice",
                      "gender": "FEMALE",
                      "meal": "VEG",
                      "seatPos": "A1"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/flight/booking/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnr").value("PNR123"));

        verify(ticketBookingInterface).getTicket(org.mockito.ArgumentMatchers.any(TicketBookingDTO.class));
    }

    @Test
    void findHistoryByEmail_success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        Ticket ticket = new Ticket();
        ticket.setPnr("PNR123");

        when(ticketDetailsInterface.findHistoryByEmail("john@example.com")).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/flight/booking/history/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pnr").value("PNR123"));

        verify(ticketDetailsInterface).findHistoryByEmail("john@example.com");
    }

    @Test
    void cancelTicket_success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        mockMvc.perform(delete("/api/flight/booking/cancel/PNR123"))
                .andExpect(status().isNoContent());

        verify(ticketDetailsInterface).cancelTicket("PNR123");
    }
}
