package com.example.demo.user.dto;

import lombok.Getter;

@Getter
public class UserResponseDto {
    private String password;
    private String email;

    public UserResponseDto(String password, String email) {
        this.password = password;
        this.email = email;
    }
}
