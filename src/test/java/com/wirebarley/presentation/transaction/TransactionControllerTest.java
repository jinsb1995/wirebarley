package com.wirebarley.presentation.transaction;

import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.transaction.jpa.JpaTransactionRepository;
import com.wirebarley.infrastructure.user.jpa.JpaUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.wirebarley.domain.transaction.TransactionType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class TransactionControllerTest {

    @Autowired
    private MockMvc mvc;

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


    @DisplayName("거래 내역을 조회한다.")
    @Test
    public void retrieveTransaction() throws Exception {
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
        // then
        mvc
                .perform(
                        get("/api/v1/transactions")
                                .param("offset", "0")
                                .param("count", "10")
                                .param("userId", String.valueOf(savedUser1.getId()))
                                .param("accountId", String.valueOf(savedAccount1.getId()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].withdrawAccountNumber").value(savedAccount1.getAccountNumber()))
                .andExpect(jsonPath("$.data[0].depositAccountNumber").value(savedAccount2.getAccountNumber()))
                .andExpect(jsonPath("$.data[0].amount").value(300L))
                .andExpect(jsonPath("$.data[0].withdrawAccountBalance").value(797))
                .andExpect(jsonPath("$.data[0].depositAccountBalance").value(1300))
                .andExpect(jsonPath("$.data[0].type").value(TRANSFER.name()))
                .andExpect(jsonPath("$.data[0].sender").value("user1"))
                .andExpect(jsonPath("$.data[0].receiver").value("user2"));
    }

    @DisplayName("거래 내역을 조회 시 계좌 소유주 정보는 필수이다.")
    @Test
    public void userIdIsRequired() throws Exception {
        // given

        // when
        // then
        mvc
                .perform(
                        get("/api/v1/transactions")
                                .param("offset", "0")
                                .param("count", "10")
                                .param("accountId", String.valueOf(1111))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("계좌 소유주 정보는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("거래 내역을 조회 시 계좌번호는 필수이다.")
    @Test
    public void accountIdIsRequired() throws Exception {
        // given

        // when
        // then
        mvc
                .perform(
                        get("/api/v1/transactions")
                                .param("offset", "0")
                                .param("count", "10")
                                .param("userId", String.valueOf(1))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("계좌번호는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
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