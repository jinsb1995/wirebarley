package com.wirebarley.application.transaction;

import com.wirebarley.application.account.dto.response.TransactionResponse;
import com.wirebarley.application.transaction.dto.request.TransactionRetrieveCommand;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.exception.CustomException;
import com.wirebarley.infrastructure.transaction.jpa.JpaTransactionRepository;
import com.wirebarley.infrastructure.user.jpa.JpaUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.wirebarley.domain.transaction.TransactionType.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @AfterEach
    void tearDown() {
        jpaTransactionRepository.deleteAllInBatch();
        jpaAccountRepository.deleteAllInBatch();
        jpaUserRepository.deleteAllInBatch();
    }

    @DisplayName("거래내역을 조회한다.")
    @Test
    public void retrieveTransaction() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;

        User user1 = createUser("user1", "user1@email.com", "password1");
        User savedUser1 = userRepository.save(user1);
        Account account1 = createAccount(withdrawAccountNumber, 1234, 1000L, savedUser1);
        Account savedAccount1 = accountRepository.save(account1);

        User user2 = createUser("user2", "user2@email.com", "password2");
        User savedUser2 = userRepository.save(user2);
        Account account2 = createAccount(depositAccountNumber, 5678, 1000L, savedUser2);
        Account savedAccount2 = accountRepository.save(account2);

        Transaction transaction1 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .withdrawAccountBalance(900L)
                .amount(100L)
                .type(WITHDRAW)
                .sender("user1")
                .receiver("ATM")
                .build();
        Transaction transaction2 = Transaction.builder()
                .depositAccount(savedAccount1)
                .depositAccountBalance(1100L)
                .amount(200L)
                .type(DEPOSIT)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction3 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .depositAccount(savedAccount2)
                .withdrawAccountBalance(797L)
                .depositAccountBalance(1300L)
                .amount(300L)
                .type(TRANSFER)
                .sender("user1")
                .receiver("user2")
                .build();
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);

        // when
        TransactionRetrieveCommand command = TransactionRetrieveCommand.builder()
                .offset(0)
                .count(10)
                .userId(savedUser1.getId())
                .accountId(savedAccount1.getId())
                .type(ALL)
                .build();
        List<TransactionResponse> results = transactionService.retrieveTransaction(command);

        // then
        assertThat(results).hasSize(3)
                .extracting("withdrawAccountNumber", "depositAccountNumber", "amount", "type", "sender", "receiver")
                .containsExactlyInAnyOrder(
                        tuple(savedAccount1.getAccountNumber(), savedAccount2.getAccountNumber(), 300L, TRANSFER, "user1", "user2"),
                        tuple(null, savedAccount1.getAccountNumber(), 200L, DEPOSIT, "ATM", "user1"),
                        tuple(savedAccount1.getAccountNumber(), null, 100L, WITHDRAW, "user1", "ATM")
                );
    }

    @DisplayName("입금 내역만 조회한다.")
    @Test
    public void retrieveTransactionByDEPOSIT() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;

        User user1 = createUser("user1", "user1@email.com", "password1");
        User savedUser1 = userRepository.save(user1);
        Account account1 = createAccount(withdrawAccountNumber, 1234, 1000L, savedUser1);
        Account savedAccount1 = accountRepository.save(account1);

        User user2 = createUser("user2", "user2@email.com", "password2");
        User savedUser2 = userRepository.save(user2);
        Account account2 = createAccount(depositAccountNumber, 5678, 1000L, savedUser2);
        Account savedAccount2 = accountRepository.save(account2);

        Transaction transaction1 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .withdrawAccountBalance(900L)
                .amount(100L)
                .type(WITHDRAW)
                .sender("user1")
                .receiver("ATM")
                .build();
        Transaction transaction2 = Transaction.builder()
                .depositAccount(savedAccount1)
                .depositAccountBalance(1100L)
                .amount(200L)
                .type(DEPOSIT)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction3 = Transaction.builder()
                .depositAccount(savedAccount1)
                .depositAccountBalance(1600L)
                .amount(500L)
                .type(DEPOSIT)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction4 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .depositAccount(savedAccount2)
                .withdrawAccountBalance(797L)
                .depositAccountBalance(1300L)
                .amount(300L)
                .type(TRANSFER)
                .sender("user1")
                .receiver("user2")
                .build();
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);

        // when
        TransactionRetrieveCommand command = TransactionRetrieveCommand.builder()
                .offset(0)
                .count(10)
                .userId(savedUser1.getId())
                .accountId(savedAccount1.getId())
                .type(DEPOSIT)
                .build();
        List<TransactionResponse> results = transactionService.retrieveTransaction(command);

        // then
        assertThat(results).hasSize(2)
                .extracting("withdrawAccountNumber", "depositAccountNumber", "amount", "type", "sender", "receiver")
                .containsExactlyInAnyOrder(
                        tuple(null, savedAccount1.getAccountNumber(), 500L, DEPOSIT, "ATM", "user1"),
                        tuple(null, savedAccount1.getAccountNumber(), 200L, DEPOSIT, "ATM", "user1")
                );
    }

    @DisplayName("출금 내역만 조회한다.")
    @Test
    public void retrieveTransactionByWITHDRAW() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;

        User user1 = createUser("user1", "user1@email.com", "password1");
        User savedUser1 = userRepository.save(user1);
        Account account1 = createAccount(withdrawAccountNumber, 1234, 1000L, savedUser1);
        Account savedAccount1 = accountRepository.save(account1);

        User user2 = createUser("user2", "user2@email.com", "password2");
        User savedUser2 = userRepository.save(user2);
        Account account2 = createAccount(depositAccountNumber, 5678, 1000L, savedUser2);
        Account savedAccount2 = accountRepository.save(account2);

        Transaction transaction1 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .withdrawAccountBalance(900L)
                .amount(100L)
                .type(WITHDRAW)
                .sender("user1")
                .receiver("ATM")
                .build();
        Transaction transaction2 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .withdrawAccountBalance(700L)
                .amount(200L)
                .type(WITHDRAW)
                .sender("user1")
                .receiver("ATM")
                .build();
        Transaction transaction3 = Transaction.builder()
                .depositAccount(savedAccount1)
                .depositAccountBalance(1200L)
                .amount(500L)
                .type(DEPOSIT)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction4 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .depositAccount(savedAccount2)
                .withdrawAccountBalance(897L)
                .depositAccountBalance(1300L)
                .amount(300L)
                .type(TRANSFER)
                .sender("user1")
                .receiver("user2")
                .build();
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);

        // when
        TransactionRetrieveCommand command = TransactionRetrieveCommand.builder()
                .offset(0)
                .count(10)
                .userId(savedUser1.getId())
                .accountId(savedAccount1.getId())
                .type(WITHDRAW)
                .build();
        List<TransactionResponse> results = transactionService.retrieveTransaction(command);

        // then
        assertThat(results).hasSize(2)
                .extracting("withdrawAccountNumber", "depositAccountNumber", "amount", "type", "sender", "receiver")
                .containsExactlyInAnyOrder(
                        tuple(savedAccount1.getAccountNumber(), null, 200L, WITHDRAW, "user1", "ATM"),
                        tuple(savedAccount1.getAccountNumber(), null, 100L, WITHDRAW, "user1", "ATM")
                );
    }

    @DisplayName("계좌이체 내역만 조회한다.")
    @Test
    public void retrieveTransactionByTRANSFER() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;

        User user1 = createUser("user1", "user1@email.com", "password1");
        User savedUser1 = userRepository.save(user1);
        Account account1 = createAccount(withdrawAccountNumber, 1234, 1000L, savedUser1);
        Account savedAccount1 = accountRepository.save(account1);

        User user2 = createUser("user2", "user2@email.com", "password2");
        User savedUser2 = userRepository.save(user2);
        Account account2 = createAccount(depositAccountNumber, 5678, 1000L, savedUser2);
        Account savedAccount2 = accountRepository.save(account2);

        Transaction transaction1 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .withdrawAccountBalance(900L)
                .amount(100L)
                .type(WITHDRAW)
                .sender("user1")
                .receiver("ATM")
                .build();
        Transaction transaction2 = Transaction.builder()
                .depositAccount(savedAccount1)
                .depositAccountBalance(1400L)
                .amount(500L)
                .type(DEPOSIT)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction3 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .depositAccount(savedAccount2)
                .withdrawAccountBalance(996L)
                .depositAccountBalance(1400L)
                .amount(400L)
                .type(TRANSFER)
                .sender("user1")
                .receiver("user2")
                .build();
        Transaction transaction4 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .depositAccount(savedAccount2)
                .withdrawAccountBalance(693L)
                .depositAccountBalance(1700L)
                .amount(300L)
                .type(TRANSFER)
                .sender("user1")
                .receiver("user2")
                .build();
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);

        // when
        TransactionRetrieveCommand command = TransactionRetrieveCommand.builder()
                .offset(0)
                .count(10)
                .userId(savedUser1.getId())
                .accountId(savedAccount1.getId())
                .type(TRANSFER)
                .build();
        List<TransactionResponse> results = transactionService.retrieveTransaction(command);

        // then
        assertThat(results).hasSize(2)
                .extracting("withdrawAccountNumber", "depositAccountNumber", "amount", "type", "sender", "receiver")
                .containsExactlyInAnyOrder(
                        tuple(savedAccount1.getAccountNumber(), savedAccount2.getAccountNumber(), 400L, TRANSFER, "user1", "user2"),
                        tuple(savedAccount1.getAccountNumber(), savedAccount2.getAccountNumber(), 300L, TRANSFER, "user1", "user2")
                );
    }

    @DisplayName("거래 내역 조회 시 계좌 주인이 아니면 예외가 발생한다.")
    @Test
    public void checkOwnerWhenRetrieveTransaction() {
        // given
        long wrongUserId = 9999999L;
        long withdrawAccountNumber = 1111L;

        User user1 = createUser("user1", "user1@email.com", "password1");
        User savedUser1 = userRepository.save(user1);
        Account account1 = createAccount(withdrawAccountNumber, 1234, 1000L, savedUser1);
        Account savedAccount1 = accountRepository.save(account1);

        // when
        TransactionRetrieveCommand command = TransactionRetrieveCommand.builder()
                .offset(0)
                .count(10)
                .userId(wrongUserId)
                .accountId(savedAccount1.getId())
                .type(TRANSFER)
                .build();

        // then
        assertThatThrownBy(() -> transactionService.retrieveTransaction(command))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 소유자가 아닙니다.");
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

    private Account createAccount(long accountNumber, int password, long balance, User user) {
        return Account.builder()
                .accountNumber(accountNumber)
                .password(password)
                .balance(balance)
                .user(user)
                .registeredAt(LocalDateTime.now())
                .unregisteredAt(null)
                .build();
    }
}