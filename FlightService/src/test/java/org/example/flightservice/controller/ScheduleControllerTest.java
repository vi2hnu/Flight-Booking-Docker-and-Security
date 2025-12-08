package org.example.flightservice.controller;

import org.example.flightservice.dto.ScheduleDTO;
import org.example.flightservice.dto.SeatsDTO;
import org.example.flightservice.service.ScheduleInterface;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    @Mock
    private ScheduleInterface scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    @Test
    void getSchedule_success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();

        ScheduleDTO dto = new ScheduleDTO(1L, 1L, 1L, 2L, null, null, 100f, 10, 120);
        when(scheduleService.getSchedule(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/schedule/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id":1,
                          "flightId":1,
                          "fromCityId":1,
                          "toCityId":2,
                          "departureDate":null,
                          "departureTime":null,
                          "price":100.0,
                          "seatsAvailable":10,
                          "duration":120
                        }
                        """));

        verify(scheduleService).getSchedule(1L);
    }

    @Test
    void checkSeats_success() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();

        SeatsDTO seatsDTO = new SeatsDTO(List.of("1A", "1B"));
        when(scheduleService.checkSeats(1L, seatsDTO)).thenReturn(true);

        String jsonBody = """
                {"seats":["1A","1B"]}
                """;

        mockMvc.perform(post("/api/schedule/check/seats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(scheduleService).checkSeats(1L, seatsDTO);
    }
}
