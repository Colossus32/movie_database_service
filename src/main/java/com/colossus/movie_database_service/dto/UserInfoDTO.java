package com.colossus.movie_database_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserInfoDTO {
    private long id;
    private String email;
    private String username;
    private String name;
}
