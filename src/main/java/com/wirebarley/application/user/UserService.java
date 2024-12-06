package com.wirebarley.application.user;

import com.wirebarley.application.user.dto.response.UserResponse;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> retrieveUsers() {
        List<User> findUsers = userRepository.findAll();
        return findUsers
                .stream()
                .map(UserResponse::of)
                .toList();
    }
}
