package com.wirebarley.presentation.account;

import com.wirebarley.application.account.AccountService;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.infrastructure.common.ApiResponse;
import com.wirebarley.presentation.account.dto.request.AccountCreateRequest;
import com.wirebarley.presentation.account.dto.request.AccountDeleteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/v1/account/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAccount(@PathVariable(value = "id") Long id, @Valid @ModelAttribute AccountDeleteRequest request) {
        accountService.deleteAccount(id, request.userId());

        return ApiResponse.ok();
    }
}
