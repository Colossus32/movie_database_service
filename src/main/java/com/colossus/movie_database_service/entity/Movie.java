package com.colossus.movie_database_service.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "_movies")
public class Movie {
    @Id
    private int kinopoiskId;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private String nameRu;

    @Column(nullable = false, unique = true)
    private String posterUrl;
}
