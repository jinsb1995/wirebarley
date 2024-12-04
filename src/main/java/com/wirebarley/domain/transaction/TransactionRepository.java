package com.wirebarley.domain.transaction;

import java.time.LocalDateTime;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Long findTotalAmountByWithdrawAccountBetweenDays(Long accountNumber, LocalDateTime startDate, LocalDateTime endDate);
}
