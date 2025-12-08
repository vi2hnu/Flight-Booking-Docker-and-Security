package org.example.flightservice.service;

import org.example.flightservice.dto.SearchQueryDTO;
import org.example.flightservice.model.entity.Schedule;

import java.util.List;

public interface SearchInterface {
    List<Schedule> search(SearchQueryDTO searchQueryDTO);
}
