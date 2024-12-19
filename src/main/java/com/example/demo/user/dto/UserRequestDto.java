package com.example.demo.user.dto;

import com.example.demo.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserRequestDto {
    private String email;
    private String nickname;
    private String password;
    private String role;


    @Builder
    public UserRequestDto(String role, String email, String nickname, String password) {
        this.role = role;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public User toEntity() {
        return new User(
                this.role,
                this.email,
                this.nickname,
                this.password
        );
    }

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }
}