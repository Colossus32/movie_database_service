package com.colossus.movie_database_service.service;

import com.colossus.movie_database_service.entity.Movie;
import com.colossus.movie_database_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean isCorrectUserForSave(User user);

    void saveUser(User user);

    Optional<User> getUserById(long id);

    void deleteUserById(long id);

    List<Movie> getAllMoviesWithPagination(Integer page, Integer quantity);

    void addMoviesToFavorites(long id, List<Long> listOfMovies);

    void removeFavoritesMovies(long id, List<Long> listOfMovies);
}
