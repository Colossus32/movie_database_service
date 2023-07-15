package com.colossus.movie_database_service.controller;

import com.colossus.movie_database_service.entity.Movie;
import com.colossus.movie_database_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService service;

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies(
            @RequestParam("page") int page,
            @RequestParam("quantity") int quantity
    ) {
        return ResponseEntity.ok(service.getMoviesWithPagination(page, quantity));
    }

}
