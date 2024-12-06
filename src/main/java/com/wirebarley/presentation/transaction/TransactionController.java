package com.wirebarley.presentation.transaction;

import com.wirebarley.application.transaction.dto.response.TransactionResponse;
import com.wirebarley.application.transaction.TransactionService;
import com.wirebarley.infrastructure.common.ApiResponse;
import com.wirebarley.presentation.transaction.dto.request.TransactionRetrieveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/v1/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> retrieveTransactions(@Valid TransactionRetrieveRequest request) {

        List<TransactionResponse> results = transactionService.retrieveTransaction(request.toCommand());
        return ApiResponse.ok(results);
    }
}
