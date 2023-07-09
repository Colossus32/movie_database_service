package com.colossus.movie_database_service.service.impl;

import com.colossus.movie_database_service.repository.MovieRepository;
import com.colossus.movie_database_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository repository;
}
