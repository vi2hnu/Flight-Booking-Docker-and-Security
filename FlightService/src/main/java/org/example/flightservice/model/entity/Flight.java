package org.example.flightservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.Year;

@Data
@Entity
public class Flight implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Year year;
    private int rows;
    private int columns;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private AirLine airline;
}
