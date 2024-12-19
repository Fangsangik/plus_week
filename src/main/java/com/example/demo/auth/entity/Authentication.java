package com.example.demo.auth.entity;

import com.example.demo.user.type.Role;
import lombok.Getter;

@Getter
public class Authentication {

    private final Long id;
    private final Role role;

    public Authentication(Long id, Role role) {
        this.id = id;
        this.role = role;
    }
}
