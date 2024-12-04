package com.wirebarley.application.account;

import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.infrastructure.exception.CustomException;
import com.wirebarley.infrastructure.transaction.TransactionRepositoryAdapter;
import com.wirebarley.infrastructure.transaction.jpa.JpaTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TransferLimitCheckerTest {

    @Autowired
    private TransferLimitChecker transferLimitChecker;

    @Autowired
    private TransactionRepositoryAdapter transactionRepositoryAdapter;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @AfterEach
    void tearDown() {
        jpaTransactionRepository.deleteAllInBatch();
    }

    @DisplayName("일 이체 한도를 초과하면 예외가 발생한다.")
    @Test
    public void dailyLimitExceed() {
        // given
        long withdrawAccountNumber = 1111L;
        long amount = 1L;
        LocalDateTime now = LocalDateTime.now();

        Account withdrawAccount = Account.builder().accountNumber(withdrawAccountNumber).build();
        Account depositAccount = Account.builder().accountNumber(2222L).build();

        Transaction transaction1 = makeTransaction(withdrawAccount, depositAccount, 50_000L, now);
        Transaction transaction2 = makeTransaction(withdrawAccount, depositAccount, 50_000L, now);
        Transaction transaction3 = makeTransaction(withdrawAccount, depositAccount, amount, now);
        transactionRepositoryAdapter.save(transaction1);
        transactionRepositoryAdapter.save(transaction2);
        transactionRepositoryAdapter.save(transaction3);

        // when
        // then
        assertThatThrownBy(() -> transferLimitChecker.checkTransferLimitByPeriod(withdrawAccountNumber, amount, LocalDateTime.now()))
                .isInstanceOf(CustomException.class)
                .hasMessage("일 이체 한도를 초과했습니다.");
    }

    @DisplayName("주 이체 한도를 초과하면 예외가 발생한다.")
    @Test
    public void weeklyLimitExceed() {
        // given
        long withdrawAccountNumber = 1111L;
        long amount = 1L;
        LocalDateTime weekStartDate = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();

        Account withdrawAccount = Account.builder().accountNumber(withdrawAccountNumber).build();
        Account depositAccount = Account.builder().accountNumber(2222L).build();

        for (int i = 1; i <= 5; i++) {
            Transaction transaction = makeTransaction(withdrawAccount, depositAccount, 100_000L, weekStartDate.plusDays(i));
            transactionRepositoryAdapter.save(transaction);
        }
        Transaction transaction = makeTransaction(withdrawAccount, depositAccount, amount, weekStartDate.plusDays(6));
        transactionRepositoryAdapter.save(transaction);

        // when
        // then
        assertThatThrownBy(() -> transferLimitChecker.checkTransferLimitByPeriod(withdrawAccountNumber, amount, weekStartDate.plusDays(6)))
                .isInstanceOf(CustomException.class)
                .hasMessage("주 이체 한도를 초과했습니다.");
    }

    @DisplayName("월 이체 한도를 초과하면 예외가 발생한다.")
    @Test
    public void monthlyLimitExceed() {
        // given
        long withdrawAccountNumber = 1111L;
        long amount = 1L;
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        Account withdrawAccount = Account.builder().accountNumber(withdrawAccountNumber).build();
        Account depositAccount = Account.builder().accountNumber(2222L).build();

        for (int i = 1; i <= 20; i++) {
            Transaction transaction = makeTransaction(withdrawAccount, depositAccount, 100_000L, monthStart.plusDays(i));
            transactionRepositoryAdapter.save(transaction);
        }
        Transaction transaction = makeTransaction(withdrawAccount, depositAccount, amount, monthStart.plusDays(21));
        transactionRepositoryAdapter.save(transaction);

        // when
        // then
        assertThatThrownBy(() -> transferLimitChecker.checkTransferLimitByPeriod(withdrawAccountNumber, amount, monthStart.plusDays(21)))
                .isInstanceOf(CustomException.class)
                .hasMessage("월 이체 한도를 초과했습니다.");
    }

    private Transaction makeTransaction(Account withdrawAccount, Account depositAccount, long amount, LocalDateTime transferDate) {
        return Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .withdrawAccountBalance(100000L)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .sender("보내는 사람 이름")
                .receiver("받는 사람 이름")
                .createdAt(transferDate)
                .build();
    }
}