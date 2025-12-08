package org.example.flightservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.example.flightservice.dto.ScheduleDTO;
import org.example.flightservice.exception.CityNotFoundException;
import org.example.flightservice.exception.FlightNotFoundException;
import org.example.flightservice.exception.InvalidScheduleTimeException;
import org.example.flightservice.exception.ScheduleConflictException;
import org.example.flightservice.model.entity.City;
import org.example.flightservice.model.entity.Flight;
import org.example.flightservice.model.entity.Schedule;
import org.example.flightservice.repository.CityRepository;
import org.example.flightservice.repository.FlightRepository;
import org.example.flightservice.repository.ScheduleRepository;
import org.example.flightservice.service.AirLineInterface;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AirLineService implements AirLineInterface {

    private final ScheduleRepository scheduleRepository;
    private final FlightRepository flightRepository;
    private final CityRepository cityRepository;
    private final KafkaTemplate<String,ScheduleDTO> kafka;

    public AirLineService(ScheduleRepository scheduleRepository, FlightRepository flightRepository,
                          CityRepository cityRepository,KafkaTemplate<String,ScheduleDTO> kafka) {
        this.scheduleRepository = scheduleRepository;
        this.flightRepository = flightRepository;
        this.cityRepository = cityRepository;
        this.kafka = kafka;
    }

    @Override
    public Schedule addSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();

        Flight flight = flightRepository.findFlightById(scheduleDTO.flightId());
        //check if flight exits
        if(flight == null){
            log.error("flight not found: {}", scheduleDTO.flightId());
            throw new FlightNotFoundException("Flight not found");
        }

        int seats = flight.getColumns()*flight.getRows();
        schedule.setSeatsAvailable(seats);
        schedule.setFlight(flight);
        //check if the time is valid
        if(scheduleDTO.departureTime().isBefore(LocalDateTime.now())){
            throw new InvalidScheduleTimeException("Invalid schedule: departure time cannot be in the past.");
        }

        //check if city is valid
        City fromCity = cityRepository.findCitiesById(scheduleDTO.fromCityId());
        City toCity = cityRepository.findCitiesById(scheduleDTO.toCityId());
        if(fromCity == null || toCity == null){
            log.error("Invalid city in schedule");
            throw new CityNotFoundException("Invalid city");
        }

        schedule.setFromCity(fromCity);
        schedule.setToCity(toCity);

        List<Schedule> previousSchedule =
                scheduleRepository.findByFlight_IdAndDepartureDate(
                        schedule.getFlight().getId(),
                        scheduleDTO.departureDate()
                );

        //check if there is a conflict
        LocalDateTime newStart = scheduleDTO.departureTime();
        LocalDateTime newEnd = newStart.plusMinutes(scheduleDTO.duration());
        boolean conflict = previousSchedule.stream()
                .anyMatch(s -> {
                    LocalDateTime existingStart = s.getDepartureTime();
                    LocalDateTime existingEnd   = existingStart.plusMinutes(s.getDuration());
                    return newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd);
                });

        if(conflict){
            log.error("Schedule already exists");
            throw new ScheduleConflictException("Conflict: schedule overlaps with existing flight timings.");
        }

        schedule.setDepartureTime(scheduleDTO.departureTime());
        schedule.setDuration(scheduleDTO.duration());
        schedule.setDepartureDate(scheduleDTO.departureDate());
        schedule.setPrice(scheduleDTO.price());

        kafka.send("schedule.added",scheduleDTO);

        return scheduleRepository.save(schedule);
    }

}
