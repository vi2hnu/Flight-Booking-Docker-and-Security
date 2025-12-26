package org.example.flightservice.service;

import org.example.flightservice.dto.SearchDTO;
import org.example.flightservice.dto.SearchQueryDTO;

import java.util.List;

public interface SearchInterface {
    List<SearchDTO> search(SearchQueryDTO searchQueryDTO);
}
