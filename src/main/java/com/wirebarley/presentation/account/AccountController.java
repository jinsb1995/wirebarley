package com.wirebarley.presentation.account;

import com.wirebarley.application.account.AccountService;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.application.account.dto.response.DepositResponse;
import com.wirebarley.application.account.dto.response.TransactionResponse;
import com.wirebarley.application.account.dto.response.WithdrawResponse;
import com.wirebarley.infrastructure.common.ApiResponse;
import com.wirebarley.presentation.account.dto.request.*;
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

    @PostMapping("/v1/account/deposit")
    public ResponseEntity<ApiResponse<DepositResponse>> deposit(@Valid @RequestBody DepositRequest request) {
        DepositResponse result = accountService.deposit(request.toCommand());

        return ApiResponse.created(result);
    }

    @PostMapping("/v1/account/withdraw")
    public ResponseEntity<ApiResponse<WithdrawResponse>> withdraw(@Valid @RequestBody WithdrawRequest request) {
        WithdrawResponse result = accountService.withdraw(request.toCommand());

        return ApiResponse.created(result);
    }

    @PostMapping("/v1/account/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResponse result = accountService.transfer(request.toCommand());

        return ApiResponse.created(result);
    }
}
