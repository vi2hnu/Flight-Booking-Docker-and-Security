package org.example.flightservice.repository;

import org.example.flightservice.model.entity.City;
import org.example.flightservice.model.entity.Schedule;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    List<Schedule> findByFlight_IdAndDepartureDate(Long flightId, LocalDate date);
    List<Schedule> findByDepartureDateAndFromCityAndToCity(LocalDate departureDate, City fromCity, City toCity);
    Schedule findScheduleById(Long id);
}
