package com.colossus.movie_database_service.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "_movies")
public class Movie {
    @Id
    private int kinopoiskId;

    @Column(nullable = false, unique = true)
    private String nameEn;

    @Column(nullable = false, unique = true)
    private String nameRu;

    @Column(nullable = false, unique = true)
    private String posterUrl;
}
