package com.wirebarley.infrastructure.account;

import com.wirebarley.config.TestQueryDslConfig;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.user.User;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.user.UserRepositoryAdapter;
import com.wirebarley.infrastructure.user.jpa.JpaUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Import({TestQueryDslConfig.class, AccountRepositoryAdapter.class, UserRepositoryAdapter.class})
@DataJpaTest
class AccountRepositoryAdapterTest {

    @Autowired
    private AccountRepositoryAdapter accountRepositoryAdapter;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private JpaAccountRepository jpaAccountRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @AfterEach
    void tearDown() {
        jpaAccountRepository.deleteAllInBatch();
        jpaUserRepository.deleteAllInBatch();
    }

    @DisplayName("Account의 가장 마지막 계좌번호를 조회한다.")
    @Test
    public void findLatestAccountNumber() {
        // given
        User user = User.builder()
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepositoryAdapter.save(user);
        Account account = Account.builder()
                .accountNumber(1111L)
                .password(1234)
                .balance(1000L)
                .user(savedUser)
                .registeredAt(LocalDateTime.now())
                .unregisteredAt(null)
                .build();
        accountRepositoryAdapter.save(account);

        // when
        Long latestAccountNumber = accountRepositoryAdapter.findLatestAccountNumber();

        // then
        assertThat(latestAccountNumber).isEqualTo(1111L);
    }

    @DisplayName("Account 계좌가 하나도 없으면 null을 반환한다.")
    @Test
    public void notExistsLatestAccountNumber() {
        // given

        // when
        Long latestAccountNumber = accountRepositoryAdapter.findLatestAccountNumber();

        // then
        assertThat(latestAccountNumber).isNull();
    }


}