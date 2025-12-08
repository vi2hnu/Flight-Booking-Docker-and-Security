package org.example.bookingservice.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import org.example.bookingservice.model.enums.Status;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Entity
public class Ticket implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pnr;

    @ManyToOne
    private Users bookedByUsers;

    private Long scheduleId;

    private Long returnTripScheduleId;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Passenger> passengers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;

}
