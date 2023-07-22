package com.colossus.movie_database_service.controller;

import com.colossus.movie_database_service.entity.Movie;
import com.colossus.movie_database_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {
    private final MovieService service;

    @GetMapping("/paging")
    public ResponseEntity<List<Movie>> getPaging(
            @RequestParam("page") int page,
            @RequestParam("quantity") int quantity
    ) {
        log.info("GET movies/paging got: page {}, quantity {}", page, quantity);
        List<Movie> result = service.getMoviesWithPagination(page, quantity);
        log.info("GET movies/paging gave: page {}, quantity {}, result's size {}, status.OK", page, quantity, result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/checkers")
    public ResponseEntity<String> checkExistedMovies(@RequestParam("ids") String listOfMoviesIds) {
        log.info("GET movies/checkers got: ids {}", listOfMoviesIds);
        String correctIds = service.checkCorrectMoviesIds(listOfMoviesIds);
        if (!correctIds.equals(listOfMoviesIds)) {
            log.warn("Some of favorites movies ids aren't found in movie database\n{}\n{}", listOfMoviesIds, correctIds);
        }
        log.info("GET movies/checkers gave: correct ids {}, status.OK", correctIds);
        return ResponseEntity.ok(correctIds);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        log.info("GET movies/all");
        return service.getAllMovies();
    }
}
