package com.wirebarley.application.transaction;

import com.wirebarley.application.account.dto.response.TransactionResponse;
import com.wirebarley.application.transaction.dto.request.TransactionRetrieveCommand;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public List<TransactionResponse> retrieveTransaction(TransactionRetrieveCommand command) {
        Account findAccount = accountRepository.findById(command.accountId());

        findAccount.checkOwner(command.userId());

        List<Transaction> transactions = transactionRepository.findTransactions(command.toQuery());

        return transactions
                .stream()
                .map(TransactionResponse::of)
                .toList();
    }
}
