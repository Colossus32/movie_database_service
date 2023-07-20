package com.colossus.movie_database_service.service.impl;

import com.colossus.movie_database_service.entity.Movie;
import com.colossus.movie_database_service.entity.User;
import com.colossus.movie_database_service.repository.UserRepository;
import com.colossus.movie_database_service.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${server.port}")
    private String PORT;

    @Value("${api.version}")
    private String API_VERSION;

    @Override
    public boolean isCorrectUserForSave(User user) {

        if (!user.getUsername().matches("[a-zA-Z]+")) return false;

        Optional<User> checkEmail = repository.findByEmail(user.getEmail());
        Optional<User> checkUsername = repository.findByUsername(user.getUsername());

        return checkUsername.isEmpty() && checkEmail.isEmpty();
    }

    @Override
    public void saveUser(User user) {

        repository.save(user);
    }

    @Override
    public Optional<User> getUserById(long id) {

        return repository.findById(id);
    }

    @Override
    public void deleteUserById(long id) {

        repository.deleteById(id);
    }

    @Override
    public List<Movie> getAllMoviesWithPagination(Integer page, Integer quantity) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:%s/api/%s/movies/paging?page=%d&quantity=%d",
                        PORT, API_VERSION, page, quantity)))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), new TypeReference<>() {});

        } catch (IOException | InterruptedException e) {
            log.error("Error with call to movie service to get all with pagination");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addMoviesToFavorites(long id, List<Long> listOfMoviesIds) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < listOfMoviesIds.size(); i++) {

            if (i != listOfMoviesIds.size() - 1) builder.append(listOfMoviesIds.get(i)).append('_');

            else builder.append(listOfMoviesIds.get(i));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:%s/api/%s/movies/checkers?ids=%s", PORT, API_VERSION,builder)))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String[] idsFromResponse = response.body().split("_");
            List<Long> correctMovieIds = new ArrayList<>();

            for (String correctMovieId : idsFromResponse) correctMovieIds.add(Long.parseLong(correctMovieId));

            Optional<User> userOptional = repository.findById(id);

            if (userOptional.isEmpty()) log.error("user {} is not found in database", id);

            else {
                User fromDatabase = userOptional.get();
                List<Long> existedListOfMovies = fromDatabase.getMoviesList();
                for (Long canBeDuplicated : correctMovieIds) if (!existedListOfMovies.contains(canBeDuplicated)) {
                    existedListOfMovies.add(canBeDuplicated);
                }
                fromDatabase.setMoviesList(existedListOfMovies);
                repository.save(fromDatabase);
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error with adding movies to user {} favorites", id);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFavoritesMovies(long id, List<Long> listOfMovies) {

        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isEmpty()) log.error("user {} is not found in database", id);

        else {
            User userFromDatabase = optionalUser.get();
            List<Long> usersFavoritesMovies = userFromDatabase.getMoviesList();

            for (Long movieId : listOfMovies) usersFavoritesMovies.remove(movieId);

            userFromDatabase.setMoviesList(usersFavoritesMovies);

            repository.save(userFromDatabase);
        }
    }

    @Override
    public List<Movie> discoverMovies(long id, String loaderType) {

        Optional<User> optionalUser = repository.findById(id);

        if (optionalUser.isEmpty()) return null;

        if (loaderType.equals("inMemory")) return discoverInMemory(optionalUser.get());

        else return discoverSql(optionalUser.get());
    }

    private List<Movie> discoverSql(User user) {

        Object[] params = user.getMoviesList().toArray();

        StringBuilder questionQuantity = new StringBuilder("?");

        questionQuantity.append(",?".repeat(params.length - 1));

        String sql =String.format("SELECT kinopoisk_id, name_ru, poster_url FROM _movies WHERE kinopoisk_id NOT IN (%s)", questionQuantity) ;


        return jdbcTemplate.query(sql, params,
                (rs, rowNum) -> new Movie(
                        rs.getLong("kinopoisk_id"),
                        rs.getString("name_ru"),
                        rs.getString("poster_url")));
    }

    private List<Movie> discoverInMemory(User user) {

        List<Movie> allMoviesFromDatabase = getAllMoviesFromDatabase();
        Set<Long> discoveredMoviesIds = new HashSet<>(user.getMoviesList());
        Set<Movie> result = new HashSet<>();

        for (Movie movie : allMoviesFromDatabase) if (!discoveredMoviesIds.contains(movie.getKinopoiskId())) result.add(movie);

        return new LinkedList<>(result);
    }
    private List<Movie> getAllMoviesFromDatabase() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:%s/api/%s/movies/all", PORT, API_VERSION)))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(response.body(), new TypeReference<>() {});

        } catch (IOException | InterruptedException e) {
            log.error("Error with response from movies/all ");
            throw new RuntimeException(e);
        }
    }
}
