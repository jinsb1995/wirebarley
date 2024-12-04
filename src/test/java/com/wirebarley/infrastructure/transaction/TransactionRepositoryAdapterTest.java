package com.wirebarley.infrastructure.transaction;

import com.wirebarley.config.TestJpaAuditingConfig;
import com.wirebarley.config.TestQueryDslConfig;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.dto.TransactionRetrieveQuery;
import com.wirebarley.domain.user.User;
import com.wirebarley.infrastructure.account.AccountRepositoryAdapter;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.transaction.jpa.JpaTransactionRepository;
import com.wirebarley.infrastructure.transaction.jpa.TransactionRepositoryDSL;
import com.wirebarley.infrastructure.user.UserRepositoryAdapter;
import com.wirebarley.infrastructure.user.jpa.JpaUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static com.wirebarley.domain.transaction.TransactionType.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import(
        {
                TestQueryDslConfig.class,
                TestJpaAuditingConfig.class,
                TransactionRepositoryAdapter.class,
                UserRepositoryAdapter.class,
                AccountRepositoryAdapter.class,
                TransactionRepositoryDSL.class
        }
)
@DataJpaTest
class TransactionRepositoryAdapterTest {

    @Autowired
    private TransactionRepositoryAdapter transactionRepositoryAdapter;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Autowired
    private JpaTransactionRepository jpaTransactionRepository;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @AfterEach
    void tearDown() {
        jpaTransactionRepository.deleteAllInBatch();
        jpaAccountRepository.deleteAllInBatch();
        jpaUserRepository.deleteAllInBatch();
    }

    @DisplayName("일일 출금한 금액을 조회한다.")
    @Test
    public void getTotalAmountByOneDay() {
        // given
        long withdrawAccountNumber1 = 1111L;

        User user = createUser("user1", "user1@email.com", "password");
        User savedUser = userRepositoryAdapter.save(user);
        Account account = createAccount(withdrawAccountNumber1, 1234, 1000L, savedUser);
        Account savedAccount = accountRepositoryAdapter.save(account);

        Transaction transaction1 = Transaction.builder()
                .withdrawAccount(savedAccount)
                .withdrawAccountBalance(100000L)
                .amount(5000L)
                .type(DEPOSIT)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction2 = Transaction.builder()
                .withdrawAccount(savedAccount)
                .withdrawAccountBalance(100000L)
                .amount(2000L)
                .type(DEPOSIT)
                .sender("ATM")
                .receiver("user1")
                .build();
        transactionRepositoryAdapter.save(transaction1);
        transactionRepositoryAdapter.save(transaction2);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.toLocalDate().atStartOfDay();

        // when
        Long totalAmount = transactionRepositoryAdapter.findTotalAmountByWithdrawAccountBetweenDays(withdrawAccountNumber1, startDate, now);

        // then
        assertThat(totalAmount).isEqualTo(7000L);
    }

    @DisplayName("거래 내역 중 입금 내역을 조회한다.")
    @Test
    public void findTransactionsByDEPOSIT() {
        // given
        long accountNumber1 = 1111L;
        long accountNumber2 = 2222L;

        User user1 = createUser("user1", "user1@email.com", "password");
        User savedUser1 = userRepositoryAdapter.save(user1);
        Account account1 = createAccount(accountNumber1, 1234, 1000L, savedUser1);
        Account savedAccount1 = accountRepositoryAdapter.save(account1);

        User user2 = createUser("user2", "user2@email.com", "password");
        User savedUser2 = userRepositoryAdapter.save(user2);
        Account account2 = createAccount(accountNumber2, 1234, 1000L, savedUser2);
        Account savedAccount2 = accountRepositoryAdapter.save(account2);

        Transaction transaction1 = Transaction.builder()
                .depositAccount(savedAccount1)
                .depositAccountBalance(100000L)
                .amount(5000L)
                .type(DEPOSIT)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction2 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .depositAccount(savedAccount2)
                .withdrawAccountBalance(100000L)
                .depositAccountBalance(50000L)
                .amount(2000L)
                .type(TRANSFER)
                .sender("user1")
                .receiver("user2")
                .build();
        transactionRepositoryAdapter.save(transaction1);
        transactionRepositoryAdapter.save(transaction2);

        // when
        TransactionRetrieveQuery query = TransactionRetrieveQuery.builder()
                .offset(0)
                .count(10)
                .accountId(1L)
                .type(DEPOSIT)
                .build();
        List<Transaction> transactions = transactionRepositoryAdapter.findTransactions(query);

        // then
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getDepositAccount().getAccountNumber()).isEqualTo(accountNumber1);
        assertThat(transactions.get(0).getDepositAccountBalance()).isEqualTo(100000L);
        assertThat(transactions.get(0).getSender()).isEqualTo("ATM");
        assertThat(transactions.get(0).getReceiver()).isEqualTo("user1");
    }

    @DisplayName("거래 내역 중 출금 내역을 조회한다.")
    @Test
    public void findTransactionsByWITHDRAW() {
        // given
        long accountNumber1 = 1111L;
        long accountNumber2 = 2222L;

        User user1 = createUser("user1", "user1@email.com", "password");
        User savedUser1 = userRepositoryAdapter.save(user1);
        Account account1 = createAccount(accountNumber1, 1234, 1000L, savedUser1);
        Account savedAccount1 = accountRepositoryAdapter.save(account1);

        User user2 = createUser("user2", "user2@email.com", "password");
        User savedUser2 = userRepositoryAdapter.save(user2);
        Account account2 = createAccount(accountNumber2, 1234, 1000L, savedUser2);
        Account savedAccount2 = accountRepositoryAdapter.save(account2);

        Transaction transaction1 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .withdrawAccountBalance(10000L)
                .amount(5000L)
                .type(WITHDRAW)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction2 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .depositAccount(savedAccount2)
                .withdrawAccountBalance(100000L)
                .depositAccountBalance(50000L)
                .amount(2000L)
                .type(TRANSFER)
                .sender("user1")
                .receiver("user2")
                .build();
        transactionRepositoryAdapter.save(transaction1);
        transactionRepositoryAdapter.save(transaction2);

        // when
        TransactionRetrieveQuery query = TransactionRetrieveQuery.builder()
                .offset(0)
                .count(10)
                .accountId(1L)
                .type(WITHDRAW)
                .build();
        List<Transaction> transactions = transactionRepositoryAdapter.findTransactions(query);

        // then
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getWithdrawAccount().getAccountNumber()).isEqualTo(accountNumber1);
        assertThat(transactions.get(0).getWithdrawAccountBalance()).isEqualTo(10000L);
        assertThat(transactions.get(0).getSender()).isEqualTo("ATM");
        assertThat(transactions.get(0).getReceiver()).isEqualTo("user1");
    }

    @DisplayName("거래 내역 중 이체 내역을 조회한다.")
    @Test
    public void findTransactions() {
        // given
        long accountNumber1 = 1111L;
        long accountNumber2 = 2222L;

        User user1 = createUser("user1", "user1@email.com", "password");
        User savedUser1 = userRepositoryAdapter.save(user1);
        Account account1 = createAccount(accountNumber1, 1234, 1000L, savedUser1);
        Account savedAccount1 = accountRepositoryAdapter.save(account1);

        User user2 = createUser("user2", "user2@email.com", "password");
        User savedUser2 = userRepositoryAdapter.save(user2);
        Account account2 = createAccount(accountNumber2, 1234, 1000L, savedUser2);
        Account savedAccount2 = accountRepositoryAdapter.save(account2);

        Transaction transaction1 = Transaction.builder()
                .depositAccount(savedAccount1)
                .depositAccountBalance(10000L)
                .amount(5000L)
                .type(WITHDRAW)
                .sender("ATM")
                .receiver("user1")
                .build();
        Transaction transaction2 = Transaction.builder()
                .withdrawAccount(savedAccount1)
                .depositAccount(savedAccount2)
                .withdrawAccountBalance(100000L)
                .depositAccountBalance(50000L)
                .amount(2000L)
                .type(TRANSFER)
                .sender("user1")
                .receiver("user2")
                .build();
        transactionRepositoryAdapter.save(transaction1);
        transactionRepositoryAdapter.save(transaction2);

        // when
        TransactionRetrieveQuery query = TransactionRetrieveQuery.builder()
                .offset(0)
                .count(10)
                .accountId(2L)
                .type(TRANSFER)
                .build();
        List<Transaction> transactions = transactionRepositoryAdapter.findTransactions(query);

        // then
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getWithdrawAccount().getAccountNumber()).isEqualTo(accountNumber1);
        assertThat(transactions.get(0).getWithdrawAccountBalance()).isEqualTo(100000L);
        assertThat(transactions.get(0).getDepositAccount().getAccountNumber()).isEqualTo(accountNumber2);
        assertThat(transactions.get(0).getDepositAccountBalance()).isEqualTo(50000L);
        assertThat(transactions.get(0).getSender()).isEqualTo("user1");
        assertThat(transactions.get(0).getReceiver()).isEqualTo("user2");
    }

    private static Account createAccount(long withdrawAccountNumber, int password, long balance, User savedUser) {
        return Account.builder()
                .accountNumber(withdrawAccountNumber)
                .password(password)
                .balance(balance)
                .user(savedUser)
                .registeredAt(LocalDateTime.now())
                .unregisteredAt(null)
                .build();
    }

    private User createUser(String user1, String mail, String password) {
        return User.builder()
                .username(user1)
                .email(mail)
                .password(password)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

}