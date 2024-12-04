package com.wirebarley.presentation.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.account.AccountRepository;
import com.wirebarley.domain.user.User;
import com.wirebarley.domain.user.UserRepository;
import com.wirebarley.infrastructure.account.jpa.JpaAccountRepository;
import com.wirebarley.infrastructure.user.jpa.JpaUserRepository;
import com.wirebarley.presentation.account.dto.request.AccountCreateRequest;
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

    @AfterEach
    void tearDown() {
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
}