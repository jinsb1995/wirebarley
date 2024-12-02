package com.wirebarley.fixture;

import com.wirebarley.domain.user.User;

import java.time.LocalDateTime;

public class UserFixture {

    public static User createUserFixture(String username, String email) {

        return User.builder()
                .username(username)
                .email(email)
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
