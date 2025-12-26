package org.example.flightservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.example.flightservice.dto.SearchDTO;
import org.example.flightservice.dto.SearchQueryDTO;
import org.example.flightservice.exception.CityNotFoundException;
import org.example.flightservice.model.entity.BookedSeats;
import org.example.flightservice.model.entity.City;
import org.example.flightservice.model.entity.Schedule;
import org.example.flightservice.repository.BookedSeatsRepository;
import org.example.flightservice.repository.CityRepository;
import org.example.flightservice.repository.ScheduleRepository;
import org.example.flightservice.service.SearchInterface;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchService implements SearchInterface {

    private final ScheduleRepository scheduleRepository;
    private final CityRepository cityRepository;
    private final BookedSeatsRepository bookedSeatsRepository;

    public SearchService(ScheduleRepository scheduleRepository, CityRepository cityRepository, BookedSeatsRepository bookedSeatsRepository) {
        this.scheduleRepository = scheduleRepository;
        this.cityRepository = cityRepository;
        this.bookedSeatsRepository = bookedSeatsRepository;
    }

    @Override
    public List<SearchDTO> search(SearchQueryDTO searchQueryDTO) {

        City fromCity = cityRepository.findCityByAirportCode(searchQueryDTO.fromCityCode());

        //check if from city is valid
        if (fromCity == null) {
            log.error("City not found: {}",searchQueryDTO.fromCityCode());
            throw new CityNotFoundException("City not found: " + searchQueryDTO.fromCityCode());
        }

        //check if to city is valid
        City toCity = cityRepository.findCityByAirportCode(searchQueryDTO.toCityCode());
        if (toCity == null) {
             log.error("City not found: {}",searchQueryDTO.toCityCode());
            throw new CityNotFoundException("City not found: " + searchQueryDTO.toCityCode());
        }
        List<SearchDTO> result = new ArrayList<>();
        List<Schedule> schedules =  scheduleRepository.findByDepartureDateAndFromCityAndToCity(searchQueryDTO.date(), fromCity, toCity);

        schedules.forEach(schedule -> {
            List<BookedSeats> bookedSeats = bookedSeatsRepository.findBySchedule(schedule);
            List<String> seats = bookedSeats.stream()
                    .map(BookedSeats::getSeatPos)
                    .toList();
            result.add(new SearchDTO(schedule, seats));
        });
        return result;
    }
}
