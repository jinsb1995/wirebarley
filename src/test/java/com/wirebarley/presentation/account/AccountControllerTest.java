package com.wirebarley.presentation.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.transaction.jpa.JpaTransactionRepository;
import com.wirebarley.infrastructure.user.jpa.JpaUserRepository;
import com.wirebarley.presentation.account.dto.request.AccountCreateRequest;
import com.wirebarley.presentation.account.dto.request.DepositRequest;
import com.wirebarley.presentation.account.dto.request.TransferRequest;
import com.wirebarley.presentation.account.dto.request.WithdrawRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

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

    @DisplayName("신규 계좌를 생성한다.")
    @Test
    public void createAccount() throws Exception {
        // given

        User user = User.builder()
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        AccountCreateRequest request = AccountCreateRequest.builder()
                .password(1234)
                .balance(1000L)
                .userId(savedUser.getId())
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account")
                                .content(om.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.balance").value(1000))
                .andExpect(jsonPath("$.data.user.username").value("user1"));

    }

    @DisplayName("신규 계좌를 생성할 때 계좌 비밀번호는 필수값이다.")
    @Test
    public void passwordIsRequiredValue() throws Exception {
        // given

        User user = User.builder()
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        AccountCreateRequest request = AccountCreateRequest.builder()
                .password(null)
                .balance(1000L)
                .userId(savedUser.getId())
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account")
                                .content(om.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("계좌 비밀번호는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 계좌를 생성할 때 회원 정보는 필수값이다.")
    @Test
    public void userIdIsRequiredValue() throws Exception {
        // given

        AccountCreateRequest request = AccountCreateRequest.builder()
                .password(1234)
                .balance(1000L)
                .userId(null)
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account")
                                .content(om.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("회원 정보는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("기존 계좌를 삭제한다.")
    @Test
    public void deleteAccount() throws Exception {
        // given

        User user = User.builder()
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        Account account = Account.builder()
                .accountNumber(1111L)
                .password(1234)
                .balance(1000L)
                .user(savedUser)
                .registeredAt(LocalDateTime.now())
                .unregisteredAt(null)
                .build();
        Account savedAccount = accountRepository.save(account);

        // when
        // then
        mvc.perform(
                        delete("/api/v1/account/{id}", savedAccount.getId())
                                .param("userId", String.valueOf(savedUser.getId()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @DisplayName("본인 소유가 아닌 계좌를 삭제하려고 하면 예외가 발생한다.")
    @Test
    public void cannotDeleteOthersAccount() throws Exception {
        // given
        String wrongUserId = "2";

        User user = User.builder()
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        Account account = Account.builder()
                .accountNumber(1111L)
                .password(1234)
                .balance(1000L)
                .user(savedUser)
                .registeredAt(LocalDateTime.now())
                .unregisteredAt(null)
                .build();
        Account savedAccount = accountRepository.save(account);

        // when
        // then
        mvc.perform(
                        delete("/api/v1/account/{id}", savedAccount.getId())
                                .param("userId", wrongUserId)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("계좌 소유자가 아닙니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("계좌를 삭제할 때 회원정보는 필수이다.")
    @Test
    public void userIdIsRequired() throws Exception {
        // given
        Long accountId = 1L;

        // when
        // then
        mvc.perform(
                        delete("/api/v1/account/{id}", accountId)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("회원 정보는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("입금에 성공한다.")
    @Test
    public void deposit() throws Exception {
        // given
        long depositAccountNumber = 1111L;

        User user = createUser("입금하는사람", "user1@email.com", "password1");
        User savedUser = userRepository.save(user);
        Account account = getAccount(depositAccountNumber, 1234, 1000L, savedUser);
        accountRepository.save(account);

        DepositRequest depositRequest = DepositRequest.builder()
                .accountNumber(depositAccountNumber)
                .amount(100L)
                .sender("ATM")
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/deposit")
                                .content(om.writeValueAsString(depositRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.code").value(201),
                        jsonPath("$.status").value("CREATED"),
                        jsonPath("$.message").value("success"),
                        jsonPath("$.data.depositAccountNumber").value(1111),
                        jsonPath("$.data.amount").value(100),
                        jsonPath("$.data.depositAccountBalance").value(1100),
                        jsonPath("$.data.type").value("DEPOSIT"),
                        jsonPath("$.data.sender").value("ATM"),
                        jsonPath("$.data.receiver").value("입금하는사람")
                );
    }

    @DisplayName("입금할 때 계좌번호는 필수값이다.")
    @Test
    public void accountNumberIsRequiredWhenDeposit() throws Exception {
        // given
        DepositRequest depositRequest = DepositRequest.builder()
                .amount(100L)
                .sender("ATM")
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/deposit")
                                .content(om.writeValueAsString(depositRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("입금 계좌번호는 필수값입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("입금할 때 입금액은 필수값이다.")
    @Test
    public void amountIsRequiredWhenDeposit() throws Exception {
        // given
        long depositAccountNumber = 1111L;

        DepositRequest depositRequest = DepositRequest.builder()
                .accountNumber(depositAccountNumber)
                .sender("ATM")
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/deposit")
                                .content(om.writeValueAsString(depositRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("입금액은 필수값입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("입금할 때 보내는 사람은 필수값이다.")
    @Test
    public void senderIsRequiredWhenDeposit() throws Exception {
        // given
        long depositAccountNumber = 1111L;

        DepositRequest depositRequest = DepositRequest.builder()
                .accountNumber(depositAccountNumber)
                .amount(100L)
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/deposit")
                                .content(om.writeValueAsString(depositRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("보내는 사람을 입력해주세요."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("출금에 성공한다.")
    @Test
    public void withdraw() throws Exception {
        // given
        long withdrawAccountNumber = 1111L;

        User withdrawUser = createUser("출금하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        accountRepository.save(withdrawAccount);

        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .accountNumber(withdrawAccountNumber)
                .amount(100L)
                .userId(savedWithdrawUser.getId())
                .password(withdrawAccount.getPassword())
                .receiver("ATM")
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/withdraw")
                                .content(om.writeValueAsString(withdrawRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.code").value(201),
                        jsonPath("$.status").value("CREATED"),
                        jsonPath("$.message").value("success"),
                        jsonPath("$.data.withdrawAccountNumber").value(1111),
                        jsonPath("$.data.amount").value(100),
                        jsonPath("$.data.withdrawAccountBalance").value(900),
                        jsonPath("$.data.type").value("WITHDRAW"),
                        jsonPath("$.data.sender").value("출금하는사람"),
                        jsonPath("$.data.receiver").value("ATM")
                );
    }

    @DisplayName("입금할 때 계좌번호는 필수값이다.")
    @Test
    public void accountNumberIsRequiredWhenWithdraw() throws Exception {
        // given
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .amount(100L)
                .userId(1L)
                .password(1234)
                .receiver("ATM")
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/withdraw")
                                .content(om.writeValueAsString(withdrawRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("출금 계좌번호는 필수값입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("출금할 때 출금액은 필수값이다.")
    @Test
    public void amountIsRequiredWhenWithdraw() throws Exception {
        // given
        long withdrawAccountNumber = 1111L;
        
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .accountNumber(withdrawAccountNumber)
                .userId(1L)
                .password(1234)
                .receiver("ATM")
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/withdraw")
                                .content(om.writeValueAsString(withdrawRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("출금액은 필수값입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("출금할 때 유저정보는 필수값이다.")
    @Test
    public void userIdIsRequiredWhenWithdraw() throws Exception {
        // given
        long withdrawAccountNumber = 1111L;

        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .accountNumber(withdrawAccountNumber)
                .amount(100L)
                .password(1234)
                .receiver("ATM")
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/withdraw")
                                .content(om.writeValueAsString(withdrawRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("유저 정보는 필수값입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("출금할 때 비밀번호는 필수값이다.")
    @Test
    public void passwordIsRequiredWhenWithdraw() throws Exception {
        // given
        long withdrawAccountNumber = 1111L;

        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .accountNumber(withdrawAccountNumber)
                .amount(100L)
                .userId(1L)
                .receiver("ATM")
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/withdraw")
                                .content(om.writeValueAsString(withdrawRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("비밀번호는 필수값입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("출금할 때 받는 사람 이름은 필수값이다.")
    @Test
    public void receiverIsRequiredWhenWithdraw() throws Exception {
        // given
        long withdrawAccountNumber = 1111L;

        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .accountNumber(withdrawAccountNumber)
                .amount(100L)
                .userId(1L)
                .password(1234)
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/withdraw")
                                .content(om.writeValueAsString(withdrawRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("받는 사람을 입력해주세요."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("계좌이체에 성공한다.")
    @Test
    public void transfer() throws Exception {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        accountRepository.save(depositAccount);

        TransferRequest transferRequest = TransferRequest.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(100L)
                .accountPassword(withdrawAccount.getPassword())
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/transfer")
                                .content(om.writeValueAsString(transferRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.code").value(201),
                        jsonPath("$.status").value("CREATED"),
                        jsonPath("$.message").value("success"),
                        jsonPath("$.data.withdrawAccountNumber").value(1111),
                        jsonPath("$.data.depositAccountNumber").value(2222),
                        jsonPath("$.data.amount").value(100),
                        jsonPath("$.data.withdrawAccountBalance").value(899),
                        jsonPath("$.data.type").value("TRANSFER"),
                        jsonPath("$.data.sender").value("계좌이체하는사람"),
                        jsonPath("$.data.receiver").value("입금받는사람")
                );
    }

    @DisplayName("계좌이체시 출금 계좌번호는 필수이다.")
    @Test
    public void withdrawNumberIsRequired() throws Exception {
        // given

        TransferRequest transferRequest = TransferRequest.builder()
                .depositNumber(1111L)
                .userId(1L)
                .amount(100L)
                .accountPassword(1234)
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/transfer")
                                .content(om.writeValueAsString(transferRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("출금 계좌번호는 필수입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("계좌이체시 입금 계좌번호는 필수이다.")
    @Test
    public void depositNumberIsRequired() throws Exception {
        // given

        TransferRequest transferRequest = TransferRequest.builder()
                .withdrawNumber(1111L)
                .userId(1L)
                .amount(100L)
                .accountPassword(1234)
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/transfer")
                                .content(om.writeValueAsString(transferRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("입금 계좌번호는 필수입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("계좌이체시 계좌 소유주 정보는 필수이다.")
    @Test
    public void userIdIsRequiredWhenTransfer() throws Exception {
        // given

        TransferRequest transferRequest = TransferRequest.builder()
                .withdrawNumber(1111L)
                .depositNumber(2222L)
                .amount(100L)
                .accountPassword(1234)
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/transfer")
                                .content(om.writeValueAsString(transferRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("계좌 소유주 정보는 필수입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("계좌이체시 이체 금액은 필수이다.")
    @Test
    public void amountIsRequired() throws Exception {
        // given

        TransferRequest transferRequest = TransferRequest.builder()
                .withdrawNumber(1111L)
                .depositNumber(2222L)
                .userId(1L)
                .accountPassword(1234)
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/transfer")
                                .content(om.writeValueAsString(transferRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("이체 금액은 필수입니다."),
                        jsonPath("$.data").isEmpty()
                );
    }

    @DisplayName("계좌이체시 출금액은 필수이다.")
    @Test
    public void accountPasswordIsRequired() throws Exception {
        // given

        TransferRequest transferRequest = TransferRequest.builder()
                .withdrawNumber(1111L)
                .depositNumber(2222L)
                .userId(1L)
                .amount(100L)
                .build();

        // when
        // then
        mvc.perform(
                        post("/api/v1/account/transfer")
                                .content(om.writeValueAsString(transferRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("출금계좌 비밀번호는 필수입니다."),
                        jsonPath("$.data").isEmpty()
                );
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

    private Account getAccount(long accountNumber, int password, long balance, User user) {
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