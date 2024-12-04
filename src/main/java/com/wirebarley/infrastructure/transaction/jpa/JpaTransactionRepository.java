package com.wirebarley.infrastructure.transaction.jpa;

import com.wirebarley.infrastructure.transaction.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("""
        select sum(te.amount)
        from TransactionEntity te
        where te.withdrawAccountNumber = :withdrawAccountNumber
        and te.createdAt between :startDate and :endDate
    """)
    Long findTotalAmountByWithdrawAccountBetweenDays(
            @Param("withdrawAccountNumber") Long withdrawAccountNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
