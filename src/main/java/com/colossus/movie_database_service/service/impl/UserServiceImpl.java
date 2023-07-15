package com.colossus.movie_database_service.service.impl;

import com.colossus.movie_database_service.entity.User;
import com.colossus.movie_database_service.repository.UserRepository;
import com.colossus.movie_database_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

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
}
