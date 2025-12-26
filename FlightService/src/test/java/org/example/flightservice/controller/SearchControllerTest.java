//package org.example.flightservice.controller;
//
//import org.example.flightservice.dto.SearchQueryDTO;
//import org.example.flightservice.model.entity.Schedule;
//import org.example.flightservice.service.SearchInterface;
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
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//
//@ExtendWith(MockitoExtension.class)
//class SearchControllerTest {
//
//    @Mock
//    private SearchInterface searchInterface;
//
//    @InjectMocks
//    private SearchController searchController;
//
//    @Test
//    void search_success() throws Exception {
//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
//
//        SearchQueryDTO dto = new SearchQueryDTO("MAA", "DEL", LocalDate.now());
//
//        Schedule schedule = new Schedule();
//        schedule.setId(1L);
//
//        List<Schedule> result = List.of(schedule);
//
//        when(searchInterface.search(dto)).thenReturn(result);
//
//        String jsonBody = """
//                {"fromCityCode":"MAA","toCityCode":"DEL","date":"%s"}
//                """.formatted(dto.date().toString());
//
//        String expectedJson = """
//                [{"id":1}]
//                """;
//
//        mockMvc.perform(
//                        post("/api/flight/search")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(jsonBody)
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().json(expectedJson));
//
//        verify(searchInterface).search(dto);
//    }
//}
