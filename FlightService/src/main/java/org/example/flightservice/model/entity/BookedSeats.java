package org.example.flightservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BookedSeats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatPos;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public BookedSeats(Schedule schedule, String seat) {
        this.schedule = schedule;
        this.seatPos = seat;
    }

    public BookedSeats(){}
}
