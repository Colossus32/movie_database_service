package com.colossus.movie_database_service.service.impl;

import com.colossus.movie_database_service.entity.Movie;
import com.colossus.movie_database_service.entity.User;
import com.colossus.movie_database_service.repository.UserRepository;
import com.colossus.movie_database_service.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

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
                .uri(URI.create(String.format("http://localhost:%s/api/%s/movies/all?page=%d&quantity=%d",
                        PORT, API_VERSION, page, quantity)))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), new TypeReference<List<Movie>>() {});

        } catch (IOException | InterruptedException e) {
            log.error("Error with call to movie service to get all with pagination");
            throw new RuntimeException(e);
        }
    }
}
