package com.colossus.movie_database_service.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "_users")
public class User {
    @Id
    @GeneratedValue
    private long id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    private String name;

    @ElementCollection
    private List<Integer> moviesList;

    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
