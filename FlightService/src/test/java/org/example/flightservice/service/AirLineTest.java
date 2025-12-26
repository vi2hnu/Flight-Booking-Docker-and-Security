//package org.example.flightservice.service;
//
//import org.example.flightservice.dto.ScheduleDTO;
//import org.example.flightservice.exception.CityNotFoundException;
//import org.example.flightservice.exception.FlightNotFoundException;
//import org.example.flightservice.exception.InvalidScheduleTimeException;
//import org.example.flightservice.exception.ScheduleConflictException;
//import org.example.flightservice.model.entity.City;
//import org.example.flightservice.model.entity.Flight;
//import org.example.flightservice.model.entity.Schedule;
//import org.example.flightservice.repository.CityRepository;
//import org.example.flightservice.repository.FlightRepository;
//import org.example.flightservice.repository.ScheduleRepository;
//import org.example.flightservice.service.implementation.AirLineService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.lenient;
//import static org.mockito.Mockito.when;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@ExtendWith(MockitoExtension.class)
//class AirLineTest {
//
//    @Mock
//    private ScheduleRepository scheduleRepository;
//
//    @Mock
//    private FlightRepository flightRepository;
//
//    @Mock
//    private CityRepository cityRepository;
//
//    @Mock
//    private KafkaTemplate<String,Object> kafka;
//
//    @InjectMocks
//    private AirLineService airLineService;
//
//    @Test
//    void addSchedule_throwsFlightNotFound() {
//        Flight flight = new Flight();
//        flight.setId(1L);
//        Schedule schedule = new Schedule();
//        schedule.setFlight(flight);
//
//        lenient().when(flightRepository.findFlightById(1L)).thenReturn(null);
//        ScheduleDTO dto = new ScheduleDTO(schedule);
//
//        assertThrows(FlightNotFoundException.class, () -> airLineService.addSchedule(dto));
//    }
//
//
//    @Test
//    void addSchedule_throwsInvalidScheduleTimeException() {
//        Flight flight = new Flight();
//        flight.setId(1L);
//        Schedule schedule = new Schedule();
//        schedule.setFlight(flight);
//        schedule.setDepartureTime(LocalDateTime.now().minusMinutes(120));
//
//        when(flightRepository.findFlightById(1L)).thenReturn(flight);
//        ScheduleDTO dto = new ScheduleDTO(schedule);
//
//        assertThrows(InvalidScheduleTimeException.class, () -> airLineService.addSchedule(dto));
//    }
//
//
//    @Test
//    void addSchedule_throwsCityNotFound() {
//        Flight flight = new Flight();
//        flight.setId(1L);
//        ScheduleDTO dto = new ScheduleDTO(null, 1L, 2L, 3L, LocalDate.now().plusDays(1),
//                LocalDateTime.now().plusHours(2), 500f, 100, 120);
//
//        when(flightRepository.findFlightById(1L)).thenReturn(flight);
//        when(cityRepository.findCitiesById(2L)).thenReturn(null);
//
//        assertThrows(CityNotFoundException.class,() -> airLineService.addSchedule(dto));
//    }
//
//
//    @Test
//    void addSchedule_throwsScheduleConflictException() {
//        Flight flight = new Flight();
//        flight.setId(1L);
//        flight.setRows(10);
//        flight.setColumns(6);
//
//        City fromCity = new City();
//        fromCity.setId(1L);
//
//        City toCity = new City();
//        toCity.setId(2L);
//
//        Schedule existingSchedule = new Schedule();
//        existingSchedule.setFlight(flight);
//        existingSchedule.setDepartureTime(LocalDateTime.of(2026, 12, 15, 10, 0));
//        existingSchedule.setDuration(120);
//        existingSchedule.setDepartureDate(LocalDateTime.of(2026, 12, 15, 0, 0).toLocalDate());
//
//        Schedule newSchedule = new Schedule();
//        newSchedule.setFlight(flight);
//        newSchedule.setFromCity(fromCity);
//        newSchedule.setToCity(toCity);
//        newSchedule.setDepartureTime(LocalDateTime.of(2026, 12, 15, 11, 0));
//        newSchedule.setDuration(90);
//        newSchedule.setDepartureDate(LocalDateTime.of(2026, 12, 15, 0, 0).toLocalDate());
//        newSchedule.setPrice(500);
//
//        ScheduleDTO scheduleDTO = new ScheduleDTO(newSchedule);
//
//        when(flightRepository.findFlightById(1L)).thenReturn(flight);
//        when(cityRepository.findCitiesById(1L)).thenReturn(fromCity);
//        when(cityRepository.findCitiesById(2L)).thenReturn(toCity);
//        when(scheduleRepository.findByFlight_IdAndDepartureDate(
//                1L,
//                LocalDateTime.of(2026, 12, 15, 0, 0).toLocalDate()
//        )).thenReturn(List.of(existingSchedule));
//
//        assertThrows(ScheduleConflictException.class, () -> airLineService.addSchedule(scheduleDTO));
//    }
//
//    @Test
//    void addSchedule_success() {
//        Flight flight = new Flight();
//        flight.setId(1L);
//        flight.setRows(10);
//        flight.setColumns(6);
//        City fromCity = new City();
//        fromCity.setId(2L);
//        City toCity = new City();
//        toCity.setId(3L);
//
//        ScheduleDTO dto = new ScheduleDTO(null, 1L, 2L, 3L, LocalDate.now().plusDays(1),
//                LocalDateTime.now().plusHours(2), 500f, 60, 120);
//
//        when(flightRepository.findFlightById(1L)).thenReturn(flight);
//        when(cityRepository.findCitiesById(2L)).thenReturn(fromCity);
//        when(cityRepository.findCitiesById(3L)).thenReturn(toCity);
//
//        when(scheduleRepository.findByFlight_IdAndDepartureDate(1L, dto.departureDate()))
//                .thenReturn(List.of());
//
//        when(scheduleRepository.save(any()))
//                .thenAnswer(i -> i.getArguments()[0]);
//
//        when(kafka.send(anyString(), any())).thenReturn(null);
//
//        Schedule result = airLineService.addSchedule(dto);
//        assertEquals(dto.seatsAvailable(), result.getSeatsAvailable());
//        assertEquals(dto.price(), result.getPrice());
//        assertEquals(dto.departureDate(), result.getDepartureDate());
//        assertEquals(dto.departureTime(), result.getDepartureTime());
//        assertEquals(dto.duration(), result.getDuration());
//
//    }
//
//}
