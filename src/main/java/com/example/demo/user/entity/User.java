package com.example.demo.user.entity;

import com.example.demo.user.type.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String nickname;
    private String password;
    private String status; // NORMAL, BLOCKED

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @Builder
    public User(String role, String email, String nickname, String password) {
        this.role = Role.of(role);
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public User() {}

    public void updateStatusToBlocked() {
        this.status = "BLOCKED";
    }
}
