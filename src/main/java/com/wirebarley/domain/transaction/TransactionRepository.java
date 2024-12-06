package com.wirebarley.domain.transaction;

import com.wirebarley.domain.transaction.dto.TransactionRetrieveQuery;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Long findTotalWithdrawalAmountByWithdrawAccount(Long accountNumber, LocalDateTime startDate, LocalDateTime endDate, List<TransactionType> types);

    List<Transaction> findTransactions(TransactionRetrieveQuery query);
}
