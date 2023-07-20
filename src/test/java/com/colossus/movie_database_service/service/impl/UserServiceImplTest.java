package com.colossus.movie_database_service.service.impl;

import com.colossus.movie_database_service.entity.User;
import com.colossus.movie_database_service.repository.MovieRepository;
import com.colossus.movie_database_service.repository.UserRepository;
import com.colossus.movie_database_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, jdbcTemplate);
    }

    @Test
    void testIsCorrectUserForSave_WithValidUser_ReturnsTrue() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act
        boolean result = userService.isCorrectUserForSave(user);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void testIsCorrectUserForSave_WithInvalidUsername_ReturnsFalse() {
        // Arrange
        User user = new User();
        user.setUsername("123");
        user.setEmail("test@example.com");

        // Act
        boolean result = userService.isCorrectUserForSave(user);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void testIsCorrectUserForSave_WithExistingEmail_ReturnsFalse() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        boolean result = userService.isCorrectUserForSave(user);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        //verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void testSaveUser() {
        // Arrange
        User user = new User();

        // Act
        userService.saveUser(user);

        // Assert
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserById_WithExistingId_ReturnsOptionalUser() {
        // Arrange
        long id = 1L;
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testGetUserById_WithNonExistingId_ReturnsEmptyOptional() {
        // Arrange
        long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteUserById() {
        // Arrange
        long id = 1L;

        // Act
        userService.deleteUserById(id);

        // Assert
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void testGetAllMoviesWithPagination() {
        // TODO: Implement test for getAllMoviesWithPagination method
    }

    @Test
    void testAddMoviesToFavorites() {
        // TODO: Implement test for addMoviesToFavorites method
    }

    @Test
    void testRemoveFavoritesMovies() {
        // TODO: Implement test for removeFavoritesMovies method
    }

    @Test
    void testDiscoverMovies_WithInMemoryLoaderType_ReturnsListOfMovies() {
        // TODO: Implement test for discoverMovies method with "inMemory" loader type
    }

    @Test
    void testDiscoverMovies_WithSqlLoaderType_ReturnsListOfMovies() {
        // TODO: Implement test for discoverMovies method with "sql" loader type
    }
}