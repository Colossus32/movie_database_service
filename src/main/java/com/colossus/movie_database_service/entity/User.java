package com.colossus.movie_database_service.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
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

    @Pattern(regexp = "[a-zA-Z]+", message = "Имя пользователя должно содержать только символы латинского алфавита")
    @Column(nullable = false, unique = true)
    private String username;

    private String name;

    @ElementCollection
    private List<Integer> moviesList;
}
