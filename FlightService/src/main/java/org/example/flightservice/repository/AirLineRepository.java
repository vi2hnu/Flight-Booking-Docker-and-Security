package org.example.flightservice.repository;

import org.example.flightservice.model.entity.AirLine;
import org.springframework.data.repository.CrudRepository;

public interface AirLineRepository extends CrudRepository<AirLine, Long> {

}
