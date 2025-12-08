package org.example.bookingservice.repository;

import org.example.bookingservice.model.entity.Passenger;
import org.springframework.data.repository.CrudRepository;

public  interface PassengerRepository  extends CrudRepository<Passenger, Long> {
}
