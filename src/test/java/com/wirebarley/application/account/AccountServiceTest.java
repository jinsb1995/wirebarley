package com.wirebarley.application.account;

import com.wirebarley.application.account.dto.request.AccountCreateCommand;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.exception.CustomException;
import com.wirebarley.infrastructure.user.jpa.JpaUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.wirebarley.fixture.AccountCreateCommandFixture.createCommandFixture;
import static com.wirebarley.fixture.AccountFixture.createAccountFixture;
import static com.wirebarley.fixture.UserFixture.createUserFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @AfterEach
    void tearDown() {
        jpaAccountRepository.deleteAllInBatch();
        jpaUserRepository.deleteAllInBatch();
    }

    @DisplayName("새로운 계좌를 생성한다.")
    @Test
    public void createAccount() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now();

        User user = createUserFixture("user1", "user1@email.com");
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        AccountCreateCommand command = createCommandFixture(1234, 1000L, userId);

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

    @DisplayName("유저 정보가 없으면 계좌 저장 시 예외가 발샏한다.")
    @Test
    public void createAccountWithoutUser() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now();

        AccountCreateCommand command = createCommandFixture(1234, 1000L, 1L);

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
        User user = createUserFixture("user1", "user1@email.com");
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        Account account = createAccountFixture(1111L, savedUser);
        Account savedAccount = accountRepository.save(account);

        // when
        accountService.deleteAccount(savedAccount.getId(), userId);

        // then
        assertThatThrownBy(() -> accountService.deleteAccount(savedAccount.getId(), userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("계좌가 존재하지 않습니다.");
    }
}