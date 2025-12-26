//package org.example.flightservice.controller;
//
//import org.example.flightservice.dto.ScheduleDTO;
//import org.example.flightservice.model.entity.Schedule;
//import org.example.flightservice.service.AirLineInterface;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.http.MediaType;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//
//@ExtendWith(MockitoExtension.class)
//class AirlineControllerTest {
//
//    @Mock
//    private AirLineInterface airLineInterface;
//
//    @InjectMocks
//    private AirlineController airlineController;
//
//    @Test
//    void addSchedule_success() throws Exception {
//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(airlineController).build();
//
//        ScheduleDTO dto = new ScheduleDTO(
//                1L,
//                2L,
//                3L,
//                4L,
//                LocalDate.now(),
//                LocalDateTime.now(),
//                120F,
//                120,
//                180
//        );
//
//        Schedule schedule = new Schedule();
//        schedule.setId(1L);
//
//        when(airLineInterface.addSchedule(dto)).thenReturn(schedule);
//
//        String jsonBody = """
//                {
//                  "id":1,
//                  "flightId":2,
//                  "fromCityId":3,
//                  "toCityId":4,
//                  "departureDate":"%s",
//                  "departureTime":"%s",
//                  "price":120.0,
//                  "seatsAvailable":120,
//                  "duration":180
//                }
//                """.formatted(dto.departureDate(), dto.departureTime());
//
//        String expectedJson = """
//                {"id":1}
//                """;
//
//        mockMvc.perform(
//                        post("/api/flight/airline/inventory")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(jsonBody)
//                )
//                .andExpect(status().isCreated())
//                .andExpect(content().json(expectedJson));
//
//        verify(airLineInterface).addSchedule(dto);
//    }
//}
