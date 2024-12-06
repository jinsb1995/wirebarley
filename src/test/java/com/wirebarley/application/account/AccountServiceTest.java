package com.wirebarley.application.account;

import com.wirebarley.application.account.dto.request.AccountCreateCommand;
import com.wirebarley.application.account.dto.request.DepositCommand;
import com.wirebarley.application.account.dto.request.TransferCommand;
import com.wirebarley.application.account.dto.request.WithdrawCommand;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.application.account.dto.response.DepositResponse;
import com.wirebarley.application.transaction.dto.response.TransactionResponse;
import com.wirebarley.application.account.dto.response.WithdrawResponse;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionRepository;
import com.wirebarley.domain.transaction.TransactionType;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

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

    @DisplayName("새로운 계좌를 생성한다.")
    @Test
    public void createAccount() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now();

        User user = createUser("user1", "user1@email.com", "password");
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        AccountCreateCommand command = getAccountCreateCommand(1234, 1000L, userId);

        // when
        AccountResponse account = accountService.createAccount(command, registeredAt);

        // then
        assertThat(account.getId()).isNotNull();
        assertThat(account)
                .extracting("balance", "registeredAt", "unregisteredAt")
                .contains(1000L, registeredAt, null);
        assertThat(account.getUser())
                .extracting("id", "username", "email")
                .contains(userId, "user1", "user1@email.com");
    }

    @DisplayName("새로운 계좌를 생성하는데, Account 테이블의 마지막 계좌번호가 1111일 때 새로 생성하는 계좌번호는 1112이다.")
    @Test
    public void createAccount2() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now();

        User user = createUser("user1", "user1@email.com", "password");
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        Account accountFixture = getAccount(1111L, 1234, 1000L, savedUser);
        Account savedAccount = accountRepository.save(accountFixture);

        AccountCreateCommand command = getAccountCreateCommand(1234, 1000L, userId);

        // when
        AccountResponse account = accountService.createAccount(command, registeredAt);

        // then
        assertThat(savedAccount.getAccountNumber()).isEqualTo(1111L);
        assertThat(account.getAccountNumber()).isEqualTo(1112L);
    }

    @DisplayName("유저 정보가 없으면 계좌 저장 시 예외가 발샏한다.")
    @Test
    public void createAccountWithoutUser() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now();

        AccountCreateCommand command = getAccountCreateCommand(1234, 1000L, 1L);

        // when
        // then
        assertThatThrownBy(() -> accountService.createAccount(command, registeredAt))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저를 찾을 수 없습니다.");
    }

    @DisplayName("계좌 소유자만 계좌를 삭제할 수 있다.")
    @Test
    public void deleteAccount() {
        // given

        User user = createUser("user1", "user1@email.com", "password");
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        Account account = getAccount(1111L, 1234, 1000L, savedUser);
        Account savedAccount = accountRepository.save(account);

        // when
        accountService.deleteAccount(savedAccount.getId(), userId);

        // then
        assertThatThrownBy(() -> accountService.deleteAccount(savedAccount.getId(), userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌가 존재하지 않습니다.");
    }

    @DisplayName("입금을 한다.")
    @Test
    public void deposit() {
        // given
        long depositAccountNumber = 1111L;
        long amount = 500;

        User user = createUser("입금받는사람", "user1@email.com", "password1");
        User savedUser = userRepository.save(user);
        Account depositAccount = getAccount(depositAccountNumber, 1234, 1000L, savedUser);
        accountRepository.save(depositAccount);

        DepositCommand depositCommand = DepositCommand.builder()
                .accountNumber(depositAccountNumber)
                .amount(amount)
                .sender("ATM")
                .build();

        // when
        DepositResponse depositResponse = accountService.deposit(depositCommand);

        // then
        assertAll(
                () -> assertThat(depositResponse.getDepositAccountNumber()).isEqualTo(depositAccountNumber),
                () -> assertThat(depositResponse.getAmount()).isEqualTo(amount),
                () -> assertThat(depositResponse.getDepositAccountBalance()).isEqualTo(1500),
                () -> assertThat(depositResponse.getType()).isEqualTo(TransactionType.DEPOSIT),
                () -> assertThat(depositResponse.getSender()).isEqualTo("ATM"),
                () -> assertThat(depositResponse.getReceiver()).isEqualTo("입금받는사람")
        );
    }

    @DisplayName("입금 하려는 금액이 0원이면 예외가 발생한다.")
    @Test
    public void depositWithZeroAmount() {
        // given
        long depositAccountNumber = 1111L;

        User user = createUser("입금받는사람", "user1@email.com", "password1");
        User savedUser = userRepository.save(user);
        Account depositAccount = getAccount(depositAccountNumber, 1234, 1000L, savedUser);
        accountRepository.save(depositAccount);

        DepositCommand depositCommand = DepositCommand.builder()
                .accountNumber(depositAccountNumber)
                .amount(0L)
                .sender("ATM")
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.deposit(depositCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("0원 이하의 금액을 입금할 수 없습니다.");
    }

    @DisplayName("출금을 한다.")
    @Test
    public void withdraw() {
        // given
        long withdrawAccountNumber = 1111L;
        long amount = 500;

        User user = createUser("출금하는사람", "user1@email.com", "password1");
        User savedUser = userRepository.save(user);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedUser);
        accountRepository.save(withdrawAccount);

        WithdrawCommand withdrawCommand = WithdrawCommand.builder()
                .accountNumber(withdrawAccountNumber)
                .amount(amount)
                .userId(savedUser.getId())
                .password(1234)
                .receiver("ATM")
                .build();

        // when
        WithdrawResponse withdrawResponse = accountService.withdraw(withdrawCommand);

        // then
        assertAll(
                () -> assertThat(withdrawResponse.getWithdrawAccountNumber()).isEqualTo(withdrawAccountNumber),
                () -> assertThat(withdrawResponse.getAmount()).isEqualTo(amount),
                () -> assertThat(withdrawResponse.getWithdrawAccountBalance()).isEqualTo(500),
                () -> assertThat(withdrawResponse.getType()).isEqualTo(TransactionType.WITHDRAW),
                () -> assertThat(withdrawResponse.getSender()).isEqualTo("출금하는사람"),
                () -> assertThat(withdrawResponse.getReceiver()).isEqualTo("ATM")
        );
    }

    @DisplayName("출금 하려는 금액이 0원이면 예외가 발생한다.")
    @Test
    public void withdrawWithZeroAmount() {
        // given
        long withdrawAccountNumber = 1111L;

        User user = createUser("출금하는사람", "user1@email.com", "password1");
        User savedUser = userRepository.save(user);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedUser);
        accountRepository.save(withdrawAccount);

        WithdrawCommand withdrawCommand = WithdrawCommand.builder()
                .accountNumber(withdrawAccountNumber)
                .amount(0L)
                .userId(savedUser.getId())
                .password(1234)
                .receiver("ATM")
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.withdraw(withdrawCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("0원 이하의 금액을 출금할 수 없습니다.");
    }

    @DisplayName("출금계좌의 비밀번호가 틀리면 계좌이체를 할 수 없다.2")
    @Test
    public void withdrawCheckPassword() {
        // given
        int wrongPassword = 9012;

        long withdrawAccountNumber = 1111L;

        User user = createUser("출금하는사람", "user1@email.com", "password1");
        User savedUser = userRepository.save(user);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedUser);
        accountRepository.save(withdrawAccount);

        WithdrawCommand withdrawCommand = WithdrawCommand.builder()
                .accountNumber(withdrawAccountNumber)
                .amount(500L)
                .userId(savedUser.getId())
                .password(wrongPassword)
                .receiver("ATM")
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.withdraw(withdrawCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 비밀번호 검증에 실패했습니다.");
    }

    @DisplayName("계좌이체시 출금계좌 잔액이 이체할 금액보다 적으면 예외가 발생한다.")
    @Test
    public void withdrawCheckEnoughBalance() {
        // given
        long withdrawAccountNumber = 1111L;
        long amount = 1000L;

        User user = createUser("출금하는사람", "user1@email.com", "password1");
        User savedUser = userRepository.save(user);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedUser);
        accountRepository.save(withdrawAccount);

        WithdrawCommand withdrawCommand = WithdrawCommand.builder()
                .accountNumber(withdrawAccountNumber)
                .amount(amount)
                .userId(savedUser.getId())
                .password(1234)
                .receiver("ATM")
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.withdraw(withdrawCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 잔액이 부족합니다.");
    }

    @DisplayName("계좌이체를 한다.")
    @Test
    public void transfer() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;
        long amount = 500;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        accountRepository.save(depositAccount);

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .build();

        // when
        TransactionResponse transferResponse = accountService.transfer(transferCommand);

        // then
        assertAll(
                () -> assertThat(transferResponse.getWithdrawAccountNumber()).isEqualTo(withdrawAccountNumber),
                () -> assertThat(transferResponse.getDepositAccountNumber()).isEqualTo(depositAccountNumber),
                () -> assertThat(transferResponse.getAmount()).isEqualTo(amount),
                () -> assertThat(transferResponse.getWithdrawAccountBalance()).isEqualTo(495),
                () -> assertThat(transferResponse.getType()).isEqualTo(TransactionType.TRANSFER),
                () -> assertThat(transferResponse.getSender()).isEqualTo("계좌이체하는사람"),
                () -> assertThat(transferResponse.getReceiver()).isEqualTo("입금받는사람")
        );
    }

    @DisplayName("계좌이체시 출금계좌와 입금계좌가 동일하면 예외가 발생한다.")
    @Test
    public void transferSameAccount() {
        // given
        long depositAccountNumber = 1111L;

        long withdrawAccountNumber = 1111L;
        long amount = 500;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        accountRepository.save(depositAccount);

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.transfer(transferCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("출금계좌와 입금계좌는 동일할 수 없습니다.");
    }

    @DisplayName("계좌이체 하려는 금액이 0원이면 예외가 발생한다.")
    @Test
    public void transferZeroAmount() {
        // given
        long amount = 0;

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

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.transfer(transferCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("0원 이하의 금액을 이체할 수 없습니다.");
    }

    @DisplayName("출금계좌의 주인이 아니면 계좌이체를 할 수 없다.")
    @Test
    public void transferCheckOwner() {
        // given
        long wrongUserId = 9999L;

        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;
        long amount = 500;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        accountRepository.save(depositAccount);

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(wrongUserId)
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.transfer(transferCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 소유자가 아닙니다.");
    }

    @DisplayName("출금계좌의 비밀번호가 틀리면 계좌이체를 할 수 없다.")
    @Test
    public void transferCheckPassword() {
        // given
        int wrongPassword = 9012;

        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;
        long amount = 500;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        accountRepository.save(depositAccount);

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(wrongPassword)
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.transfer(transferCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 비밀번호 검증에 실패했습니다.");
    }

    @DisplayName("계좌이체시 출금계좌 잔액이 이체할 금액보다 적으면 예외가 발생한다.")
    @Test
    public void transferCheckEnoughBalance() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;
        long amount = 1000;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        accountRepository.save(depositAccount);

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.transfer(transferCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌 잔액이 부족합니다.");
    }

    @DisplayName("일 이체 한도를 초과하면 예외가 발생한다.")
    @Test
    public void dailyLimitExceed() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;
        long amount = 100;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        Account savedWithdrawAccount = accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        Account savedDepositAccount = accountRepository.save(depositAccount);

        LocalDateTime transferDate = LocalDateTime.now();
        Transaction transaction = makeTransaction(savedWithdrawAccount, savedDepositAccount, 100_000L, transferDate);
        transactionRepository.save(transaction);

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.transfer(transferCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("일 이체 한도를 초과했습니다.");
    }

    @DisplayName("주 이체 한도를 초과하면 예외가 발생한다.")
    @Test
    public void weeklyLimitExceed() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;
        long amount = 100;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        Account savedWithdrawAccount = accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        Account savedDepositAccount = accountRepository.save(depositAccount);

        LocalDateTime weekStartDate = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        for (int i = 1; i <= 5; i++) {
            Transaction transaction = makeTransaction(savedWithdrawAccount, savedDepositAccount, 100_000L, weekStartDate.plusDays(i));
            transactionRepository.save(transaction);
        }

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .transferDate(weekStartDate.plusDays(6))
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.transfer(transferCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("주 이체 한도를 초과했습니다.");
    }

    @DisplayName("월 이체 한도를 초과하면 예외가 발생한다.")
    @Test
    public void monthlyLimitExceed() {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;
        long amount = 100;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        Account savedWithdrawAccount = accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        Account savedDepositAccount = accountRepository.save(depositAccount);

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        for (int i = 1; i <= 20; i++) {
            Transaction transaction = makeTransaction(savedWithdrawAccount, savedDepositAccount, 100_000L, monthStart.plusDays(i));
            transactionRepository.save(transaction);
        }

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .transferDate(monthStart.plusDays(21))
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.transfer(transferCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage("월 이체 한도를 초과했습니다.");
    }

    @DisplayName("1000원이 있는 계좌에서 100원씩 이체를 하면 수수료 때문에 9번만 이체를 할 수 있다.")
    @Test
    public void concurrencyTestWhileTransfer() throws InterruptedException {
        // given
        long withdrawAccountNumber = 1111L;
        long depositAccountNumber = 2222L;
        long amount = 100;

        User withdrawUser = createUser("계좌이체하는사람", "user1@email.com", "password1");
        User savedWithdrawUser = userRepository.save(withdrawUser);
        Account withdrawAccount = getAccount(withdrawAccountNumber, 1234, 1000L, savedWithdrawUser);
        accountRepository.save(withdrawAccount);

        User depositUser = createUser("입금받는사람", "user2@email.com", "password2");
        User savedDepositUser = userRepository.save(depositUser);
        Account depositAccount = getAccount(depositAccountNumber, 5678, 1000L, savedDepositUser);
        accountRepository.save(depositAccount);

        TransferCommand transferCommand = TransferCommand.builder()
                .withdrawNumber(withdrawAccountNumber)
                .depositNumber(depositAccountNumber)
                .userId(savedWithdrawUser.getId())
                .amount(amount)
                .accountPassword(withdrawAccount.getPassword())
                .build();

        // when
        int threadCount = 12;
        ExecutorService es = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        AtomicInteger failCount = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            es.execute(() -> {
                try {
                    accountService.transfer(transferCommand);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        es.shutdown();

        // then
        assertThat(failCount.get()).isEqualTo(3);
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

    private AccountCreateCommand getAccountCreateCommand(int password, long balance, Long userId) {
        return AccountCreateCommand.builder()
                .password(password)
                .balance(balance)
                .userId(userId)
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