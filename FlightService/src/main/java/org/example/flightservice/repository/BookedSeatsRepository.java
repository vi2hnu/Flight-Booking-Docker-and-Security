package org.example.flightservice.repository;

import org.example.flightservice.model.entity.BookedSeats;
import org.example.flightservice.model.entity.Schedule;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface BookedSeatsRepository extends CrudRepository<BookedSeats, Long> {
    boolean existsBySchedule_IdAndSeatPos(Long scheduleId, String seatPos);
    void deleteBySchedule_IdAndSeatPos(Long scheduleId, String seatPos);
    List<BookedSeats> findBySchedule(Schedule schedule);
}
