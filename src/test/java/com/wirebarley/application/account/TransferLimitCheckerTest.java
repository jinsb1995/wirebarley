package com.wirebarley.application.account;

import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
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
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

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

        User user1 = createUser("userId1", "user1@email.com", "password1");
        User user2 = createUser("userId2", "user2@email.com", "password1");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        Account withdrawAccount = Account.builder().accountNumber(withdrawAccountNumber).user(savedUser1).build();
        Account depositAccount = Account.builder().accountNumber(2222L).user(savedUser2).build();
        Account savedWithdrawAccount = accountRepository.save(withdrawAccount);
        Account savedDepositAccount = accountRepository.save(depositAccount);

        Transaction transaction1 = makeTransaction(savedWithdrawAccount, savedDepositAccount, 50_000L, now);
        Transaction transaction2 = makeTransaction(savedWithdrawAccount, savedDepositAccount, 50_000L, now);
        Transaction transaction3 = makeTransaction(savedWithdrawAccount, savedDepositAccount, amount, now);
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

        User user1 = createUser("userId1", "user1@email.com", "password1");
        User user2 = createUser("userId2", "user2@email.com", "password1");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        Account withdrawAccount = Account.builder().accountNumber(withdrawAccountNumber).user(savedUser1).build();
        Account depositAccount = Account.builder().accountNumber(2222L).user(savedUser2).build();
        Account savedWithdrawAccount = accountRepository.save(withdrawAccount);
        Account savedDepositAccount = accountRepository.save(depositAccount);

        for (int i = 1; i <= 5; i++) {
            Transaction transaction = makeTransaction(savedWithdrawAccount, savedDepositAccount, 100_000L, weekStartDate.plusDays(i));
            transactionRepositoryAdapter.save(transaction);
        }
        Transaction transaction = makeTransaction(savedWithdrawAccount, savedDepositAccount, amount, weekStartDate.plusDays(6));
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

        User user1 = createUser("userId1", "user1@email.com", "password1");
        User user2 = createUser("userId2", "user2@email.com", "password1");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        Account withdrawAccount = Account.builder().accountNumber(withdrawAccountNumber).user(savedUser1).build();
        Account depositAccount = Account.builder().accountNumber(2222L).user(savedUser2).build();
        Account savedWithdrawAccount = accountRepository.save(withdrawAccount);
        Account savedDepositAccount = accountRepository.save(depositAccount);

        for (int i = 1; i <= 20; i++) {
            Transaction transaction = makeTransaction(savedWithdrawAccount, savedDepositAccount, 100_000L, monthStart.plusDays(i));
            transactionRepositoryAdapter.save(transaction);
        }
        Transaction transaction = makeTransaction(savedWithdrawAccount, savedDepositAccount, amount, monthStart.plusDays(21));
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

    private User createUser(String username, String mail, String password) {
        return User.builder()
                .username(username)
                .email(mail)
                .password(password)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}