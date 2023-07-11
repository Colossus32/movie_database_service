package com.colossus.movie_database_service.controller;

import com.colossus.movie_database_service.entity.User;
import com.colossus.movie_database_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void user_registration_should_be_ok() throws Exception {

        User user = new User( "johndoe@example.com", "John Doe");

        when(service.isCorrectUserForSave(any(User.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"John Doe\",\"email\": \"johndoe@example.com\"}"))
                .andExpect(status().isOk())
                .andReturn();

        byte[] response = result.getResponse().getContentAsByteArray();
        ObjectMapper mapper = new ObjectMapper();
        User fromResponse = mapper.readValue(response, User.class);

        assertEquals(user.getEmail(), fromResponse.getEmail());
        assertEquals(user.getUsername(), fromResponse.getUsername());
    }

    @Test
    void user_registration_should_be_bad_request() throws Exception {

        User user = new User("John Doe", "johndoe@example.com");

        when(service.isCorrectUserForSave(any(User.class))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}"))
                .andExpect(status().isBadRequest());
    }
}