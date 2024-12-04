package com.wirebarley.infrastructure.transaction;

import com.wirebarley.config.TestJpaAuditingConfig;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.infrastructure.transaction.jpa.JpaTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Import({TestJpaAuditingConfig.class, TransactionRepositoryAdapter.class})
@DataJpaTest
class TransactionRepositoryAdapterTest {

    @Autowired
    private TransactionRepositoryAdapter transactionRepositoryAdapter;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @AfterEach
    void tearDown() {
        jpaTransactionRepository.deleteAllInBatch();
    }

    @DisplayName("일일 출금한(이체한) 금액을 조회한다..")
    @Test
    public void getTotalAmountByOneDay() {
        // given
        long withdrawAccountNumber = 1111L;
        Transaction transaction1 = makeTransaction(withdrawAccountNumber, 2222L, 5000L);
        Transaction transaction2 = makeTransaction(withdrawAccountNumber, 3333L, 2000L);
        transactionRepositoryAdapter.save(transaction1);
        transactionRepositoryAdapter.save(transaction2);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.toLocalDate().atStartOfDay();

        // when
        Long totalAmount = transactionRepositoryAdapter.findTotalAmountByWithdrawAccountBetweenDays(withdrawAccountNumber, startDate, now);

        // then
        assertThat(totalAmount).isEqualTo(7000L);
    }

    private Transaction makeTransaction(long withdrawAccountNumber, long depositAccountNumber, long amount) {
        return Transaction.builder()
                .withdrawAccountNumber(withdrawAccountNumber)
                .depositAccountNumber(depositAccountNumber)
                .withdrawAccountBalance(100000L)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .sender("보내는 사람 이름")
                .receiver("받는 사람 이름")
                .build();
    }
}