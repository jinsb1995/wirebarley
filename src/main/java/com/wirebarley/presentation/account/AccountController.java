package com.wirebarley.presentation.account;

import com.wirebarley.application.account.AccountService;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.infrastructure.common.ApiResponse;
import com.wirebarley.presentation.account.dto.request.AccountCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/v1/account")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        AccountResponse result = accountService.createAccount(request.toCommand(), LocalDateTime.now());

        return ApiResponse.created(result);
    }
}
