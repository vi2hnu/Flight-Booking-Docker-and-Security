package org.example.flightservice.service;

import org.example.flightservice.dto.SearchQueryDTO;
import org.example.flightservice.exception.CityNotFoundException;
import org.example.flightservice.model.entity.City;
import org.example.flightservice.model.entity.Schedule;
import org.example.flightservice.repository.CityRepository;
import org.example.flightservice.repository.ScheduleRepository;
import org.example.flightservice.service.implementation.SearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private SearchService searchService;

    @Test
    void search_throwsCityNotFound_fromCity() {
        SearchQueryDTO dto = new SearchQueryDTO("BLR", "DEL", LocalDate.now());

        when(cityRepository.findCityByAirportCode("BLR")).thenReturn(null);

        assertThrows(CityNotFoundException.class, () -> searchService.search(dto));
    }

    @Test
    void search_throwsCityNotFound_toCity() {
        SearchQueryDTO dto = new SearchQueryDTO("BLR", "DEL", LocalDate.now());
        City fromCity = new City();

        when(cityRepository.findCityByAirportCode("BLR")).thenReturn(fromCity);
        when(cityRepository.findCityByAirportCode("DEL")).thenReturn(null);

        assertThrows(CityNotFoundException.class, () -> searchService.search(dto));
    }

    @Test
    void search_success() {
        SearchQueryDTO dto = new SearchQueryDTO("BLR", "DEL", LocalDate.now());
        City fromCity = new City();
        City toCity = new City();
        List<Schedule> schedules = List.of(new Schedule());

        when(cityRepository.findCityByAirportCode("BLR")).thenReturn(fromCity);
        when(cityRepository.findCityByAirportCode("DEL")).thenReturn(toCity);
        when(scheduleRepository.findByDepartureDateAndFromCityAndToCity(
                dto.date(), fromCity, toCity
        )).thenReturn(schedules);
        List<Schedule> result = searchService.search(dto);

        assertEquals(1, result.size());
    }

}
