package com.colossus.movie_database_service.controller;

import com.colossus.movie_database_service.dto.UserEditDTO;
import com.colossus.movie_database_service.dto.UserInfoDTO;
import com.colossus.movie_database_service.entity.Movie;
import com.colossus.movie_database_service.entity.User;
import com.colossus.movie_database_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService service;


    @PostMapping("/registrations")
    public ResponseEntity<User> userRegistration(@RequestBody User user) {

        if(service.isCorrectUserForSave(user)) {
            service.saveUser(user);
            return ResponseEntity.ok(user);
        } else {
            log.error("Error to register user:\n{}", user);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDTO> getUserInformation(
            @RequestHeader("User-Id") long headerId,
            @PathVariable("id") long id) {

        if (headerId != id) {
            log.error("Error to get user info bcz header User-Id doesn't equal requested id:\n{} - {}", headerId, id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Optional<User> optionalUser = service.getUserById(id);

        if (optionalUser.isEmpty()) {
            log.error("Error to get info from user with id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        User userFromDatabase = optionalUser.get();
        UserInfoDTO userInfo = new UserInfoDTO(
                userFromDatabase.getId(),
                userFromDatabase.getEmail(),
                userFromDatabase.getUsername(),
                userFromDatabase.getName()
        );

        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEditDTO> editUsernameAndName(
            @RequestHeader("User-Id") long headerId,
            @PathVariable long id,
            @RequestBody UserEditDTO userEditDTO) {

        if (headerId != id) {
            log.error("Error to get user info bcz header User-Id doesn't equal requested id:\n{} - {}", headerId, id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Optional<User> optionalUser = service.getUserById(id);

        if (optionalUser.isEmpty()) {
            log.error("Error to update info for user with id {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        User fromDatabase = optionalUser.get();

        if (userEditDTO.getUsername() != null) fromDatabase.setUsername(userEditDTO.getUsername());
        if (userEditDTO.getName() != null) fromDatabase.setName(userEditDTO.getName());

        service.saveUser(fromDatabase);

        return ResponseEntity.ok(userEditDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(
            @RequestHeader("User-Id") long headerId,
            @PathVariable("id") long id) {

        if (headerId != id) {
            log.error("Error to get user info bcz header User-Id doesn't equal requested id:\n{} - {}", headerId, id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> userFromDatabase = service.getUserById(id);

        if (userFromDatabase.isEmpty()) {
            log.error("Error to delete user with id {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        service.deleteUserById(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/favorites")
    public ResponseEntity addToFavorites(@RequestHeader("User-Id") long headerId,
                                           @PathVariable("id") long id,
                                           @RequestBody List<Long> listOfMovies) {

        if (headerId != id) {
            log.error("Error to get user info bcz header User-Id doesn't equal requested id:\n{} - {}", headerId, id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> userFromDatabase = service.getUserById(id);

        if (userFromDatabase.isEmpty()) {
            log.error("Error to add movies to user with id {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            service.addMoviesToFavorites(id, listOfMovies);
            return ResponseEntity.ok().build();
        }
    }
    @DeleteMapping("{id}/favorites")
    public ResponseEntity removeFavorites(@RequestHeader("User-Id") long headerId,
                                         @PathVariable("id") long id,
                                         @RequestBody List<Long> listOfMovies) {

        if (headerId != id) {
            log.error("Error to get user info bcz header User-Id doesn't equal requested id:\n{} - {}", headerId, id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> userFromDatabase = service.getUserById(id);

        if (userFromDatabase.isEmpty()) {
            log.error("Error to add movies to user with id {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            service.removeFavoritesMovies(id, listOfMovies);
            return ResponseEntity.ok().build();
        }

    }

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getAllMovies(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "quantity", required = false) Integer quantity) {

        if (page == null && quantity == null) {
            page = 1;
            quantity = 15;
        }
        if((page == null || quantity == null)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        if (page <= 0 || quantity <= 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        return ResponseEntity.ok(service.getAllMoviesWithPagination(page, quantity));
    }

    @GetMapping("/discover/{id}")
    public ResponseEntity<List<Movie>> discoverMovies(@RequestHeader("User-Id") long headerId,
                                      @PathVariable long id,
                                      @RequestParam(value = "loaderType") String loaderType) {

        if (headerId != id) {
            log.error("Error to get user info bcz header User-Id doesn't equal requested id:\n{} - {}", headerId, id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (service.getUserById(id).isEmpty()) {
            log.error("Error to get user with id {} in discover", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (loaderType.equals("inMemory") || loaderType.equals("sql")) return ResponseEntity.ok(service.discoverMovies(id,loaderType));
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}
