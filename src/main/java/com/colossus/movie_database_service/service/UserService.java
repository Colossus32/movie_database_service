package com.colossus.movie_database_service.service;

import com.colossus.movie_database_service.entity.User;

import java.util.Optional;

public interface UserService {
    boolean isCorrectUserForSave(User user);

    void saveUser(User user);

    Optional<User> getUserById(long id);

    void deleteUserById(long id);
}
