package com.wirebarley.application.account;

import com.wirebarley.application.account.dto.request.AccountCreateCommand;
import com.wirebarley.application.account.dto.response.AccountResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

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

        User user = User.builder()
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        AccountCreateCommand command = AccountCreateCommand.builder()
                .password(1234)
                .balance(1000L)
                .userId(userId)
                .build();

        // when
        AccountResponse account = accountService.createAccount(command, registeredAt);

        // then
        assertThat(account.getId()).isNotNull();
        assertThat(account)
                .extracting("balance", "registeredAt")
                .contains(1000L, registeredAt);
        assertThat(account.getUser())
                .extracting("id", "username", "email")
                .contains(userId, "user1", "user1@email.com");
    }

    @DisplayName("유저 정보가 없으면 계좌 저장 시 예외가 발샏한다.")
    @Test
    public void createAccountWithoutUser() {
        // given
        LocalDateTime registeredAt = LocalDateTime.now();

        AccountCreateCommand command = AccountCreateCommand.builder()
                .password(1234)
                .balance(1000L)
                .userId(1L)
                .build();

        // when
        // then
        assertThatThrownBy(() -> accountService.createAccount(command, registeredAt))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저를 찾을 수 없습니다.");
    }

}