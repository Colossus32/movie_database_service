package com.colossus.movie_database_service.service.impl;

import com.colossus.movie_database_service.repository.UserRepository;
import com.colossus.movie_database_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
}
