package org.example.flightservice.controller;

import jakarta.validation.Valid;
import org.example.flightservice.dto.SearchQueryDTO;
import org.example.flightservice.model.entity.Schedule;
import org.example.flightservice.service.SearchInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flight/search")
public class SearchController {

    private final SearchInterface searchInterface;

    public SearchController(SearchInterface searchInterface) {
        this.searchInterface = searchInterface;
    }

    @PostMapping()
    public ResponseEntity<List<Schedule>> search(@Valid @RequestBody SearchQueryDTO searchQueryDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(searchInterface.search(searchQueryDTO));
    }
}
