package com.colossus.movie_database_service.service;

import com.colossus.movie_database_service.entity.Movie;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MovieService {
    void checkPremiers();

    List<Movie> getMoviesWithPagination(int page, int quantity);

    String checkCorrectMoviesIds(String listOfMoviesIds);

    ResponseEntity<List<Movie>> getAllMovies();
}
