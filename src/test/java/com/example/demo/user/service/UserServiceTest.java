package com.example.demo.user.service;

import com.example.demo.user.dto.UserRequestDto;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.type.Role;
import com.example.demo.util.PasswordEncoder;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {


    @Autowired
    private UserService userService;

    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
                .password("password123") // 예시 비밀번호
                .email("test@test.com") // 유효한 이메일 포맷으로 변경
                .role("user")
                .build();
    }

    @Test
    void signupWithEmail() {
        // 회원가입 수행
        UserResponseDto userResponseDto = userService.signupWithEmail(userRequestDto);

        // 이메일이 원본과 동일한지 확인
        assertEquals(userRequestDto.getEmail(), userResponseDto.getEmail());
        System.out.println("Email: " + userRequestDto.getEmail());

        assertEquals("test@test.com", userRequestDto.getEmail());

        // 비밀번호 암호화 검증
        // 디버깅용 출력
        System.out.println("Original Password: " + userRequestDto.getPassword()); // test
        System.out.println("Encoded Password: " + userResponseDto.getPassword()); // bcrypt 해시 값

        // 원래 비밀번호와 인코딩된 비밀번호가 매칭되는지 확인
        assertTrue(PasswordEncoder.matches("password123", userResponseDto.getPassword()));
    }
}