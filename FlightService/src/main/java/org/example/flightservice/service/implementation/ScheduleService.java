package org.example.flightservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.example.flightservice.dto.ScheduleDTO;
import org.example.flightservice.dto.SeatsDTO;
import org.example.flightservice.exception.ScheduleNotFoundException;
import org.example.flightservice.model.entity.BookedSeats;
import org.example.flightservice.model.entity.Schedule;
import org.example.flightservice.repository.BookedSeatsRepository;
import org.example.flightservice.repository.ScheduleRepository;
import org.example.flightservice.service.ScheduleInterface;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
public class ScheduleService implements ScheduleInterface {

    private final ScheduleRepository scheduleRepository;
    private final BookedSeatsRepository bookedSeatsRepository;
    private static final String scheduleNotFound = "Schedule not found: {}";
    private final RedissonClient redisson;
    private final RedissonClient redissonClient;

    public ScheduleService(ScheduleRepository scheduleRepository, BookedSeatsRepository bookedSeatsRepository, RedissonClient redisson, RedissonClient redissonClient) {
        this.scheduleRepository = scheduleRepository;
        this.bookedSeatsRepository = bookedSeatsRepository;
        this.redisson = redisson;
        this.redissonClient = redissonClient;
    }

    @Override
    public ScheduleDTO getSchedule(Long scheduleId) {
       Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
       if(schedule == null){
           log.error(scheduleNotFound,scheduleId);
           throw new ScheduleNotFoundException("Schedule not found: "+scheduleId);
       }
       return new ScheduleDTO(schedule);
    }

    @Override
    public boolean checkSeats(Long scheduleId, SeatsDTO seatsDTO) {
        return seatsDTO.seats().stream()
                .anyMatch(seat-> bookedSeatsRepository.existsBySchedule_IdAndSeatPos(scheduleId,seat));
    }

    @Override
    public boolean reserveSeats(Long scheduleId, SeatsDTO seatsDTO) {
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        if(schedule==null){
            log.error("Schedule not found");
            throw new ScheduleNotFoundException("Schedule not found");
        }
        String seats = seatsDTO.seats().toString();
        String lockKey = scheduleId.toString()+seats; // 5[12A.12b]
        RLock lock =  redissonClient.getLock(lockKey);

        if(!lock.tryLock()){
            return false;
        }
        log.info("locked {}",lockKey);

        if(checkSeats(scheduleId,seatsDTO)){
            return false;
        }

        seatsDTO.seats().stream()
                .forEach(seat ->bookedSeatsRepository.save(new BookedSeats(schedule,seat)));

        lock.unlock();
        log.info("unlocked {}",lockKey);
        return true;
    }

    @Transactional
    @Override
    public void deleteSeats(Long scheduleId, SeatsDTO seatsDTO) {
        seatsDTO.seats().stream()
                .forEach(seat -> bookedSeatsRepository.deleteBySchedule_IdAndSeatPos(scheduleId,seat));

        addSeats(scheduleId,seatsDTO.seats().size());
    }

    @Override
    public void addSeats(Long scheduleId, int seats){
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        if(schedule == null){
           log.error(scheduleNotFound,scheduleId);
           throw new ScheduleNotFoundException("Schedule not found: "+scheduleId);
        }

        schedule.setSeatsAvailable(seats+schedule.getSeatsAvailable());
        scheduleRepository.save(schedule);
    }
}
