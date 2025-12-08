package org.example.flightservice.repository;

import org.example.flightservice.model.entity.Flight;
import org.springframework.data.repository.CrudRepository;

public interface FlightRepository extends CrudRepository<Flight, Long> {
    Flight findFlightById(Long id);
}
