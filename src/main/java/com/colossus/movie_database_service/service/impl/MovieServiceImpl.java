package com.colossus.movie_database_service.service.impl;

import com.colossus.movie_database_service.entity.Movie;
import com.colossus.movie_database_service.repository.MovieRepository;
import com.colossus.movie_database_service.service.MovieService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class MovieServiceImpl implements MovieService {
    private final MovieRepository repository;

    @Scheduled(cron = "*/10 * * * * *")
    @Override
    public void checkPremiers() {

        String apikey = getApikey();

        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        String month = String.valueOf(localDate.getMonth());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://kinopoiskapiunofficial.tech/api/v2.2/films/premieres?year=%d&month=%s", year, month)))
                .header("accept", "application/json")
                .header("X-API-KEY", apikey)
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode itemsNode =jsonNode.get("items");
            TypeReference<List<Movie>> typeReference = new TypeReference<>() {};

            List<Movie> movieListFromApi = objectMapper.readValue(itemsNode.toString(), typeReference);

            for(Movie movie : movieListFromApi) {
                if (repository.findById(movie.getKinopoiskId()).isEmpty()) repository.save(movie);
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error with response from kinopoisk api.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Movie> getMoviesWithPagination(int page, int quantity) {

        List<Movie> listFromDatabase = repository.findAll();
        Collections.reverse(listFromDatabase);

        //bcz index in list starts from 0
        int start = page * (quantity - 1) - 1;

        if (start >= listFromDatabase.size()) return new ArrayList<>();

        int end = start + quantity >= listFromDatabase.size() ? listFromDatabase.size() - 1 : start + quantity;

        return listFromDatabase.subList(start, end);
    }

    private String getApikey() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("apikey.txt"));
            String apikey = reader.readLine();
            reader.close();
            return apikey;

        } catch (FileNotFoundException e) {
            log.error("Error with apikey.txt\n");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Error with reading from apikey.txt");
            throw new RuntimeException(e);
        }
    }
}
