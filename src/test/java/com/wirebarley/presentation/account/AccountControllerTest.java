package com.wirebarley.presentation.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}