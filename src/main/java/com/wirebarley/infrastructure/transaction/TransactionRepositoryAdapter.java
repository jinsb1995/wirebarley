package com.wirebarley.infrastructure.transaction;

import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionRepository;
import com.wirebarley.domain.transaction.dto.TransactionRetrieveQuery;
import com.wirebarley.infrastructure.transaction.entity.TransactionEntity;
import com.wirebarley.infrastructure.transaction.jpa.JpaTransactionRepository;
import com.wirebarley.infrastructure.transaction.jpa.TransactionRepositoryDSL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final JpaTransactionRepository jpaTransactionRepository;
    private final TransactionRepositoryDSL transactionRepositoryDSL;

    @Override
    public Transaction save(Transaction transaction) {

        TransactionEntity transactionEntity = TransactionEntity.create(transaction);
        return jpaTransactionRepository.save(transactionEntity)
                .toDomain();
    }

    @Override
    public Long findTotalAmountByWithdrawAccountBetweenDays(Long accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        Long totalAmount = jpaTransactionRepository.findTotalAmountByWithdrawAccountBetweenDays(accountNumber, startDate, endDate);
        return totalAmount == null ? 0 : totalAmount;
    }

    @Override
    public List<Transaction> findTransactions(TransactionRetrieveQuery dto) {
        return transactionRepositoryDSL.findTransactions(dto)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
