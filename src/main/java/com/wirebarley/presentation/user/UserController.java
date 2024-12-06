package com.wirebarley.presentation.user;

import com.wirebarley.application.user.UserService;
import com.wirebarley.application.user.dto.response.UserResponse;
import com.wirebarley.infrastructure.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/v1/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> retrieveUsers() {
        List<UserResponse> userResponses = userService.retrieveUsers();

        return ApiResponse.ok(userResponses);
    }
}
