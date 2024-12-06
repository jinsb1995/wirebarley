package com.wirebarley.restdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirebarley.application.account.AccountService;
import com.wirebarley.application.account.dto.response.AccountResponse;
import com.wirebarley.application.account.dto.response.DepositResponse;
import com.wirebarley.application.account.dto.response.WithdrawResponse;
import com.wirebarley.application.transaction.dto.response.TransactionResponse;
import com.wirebarley.application.user.dto.response.UserResponse;
import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.domain.user.User;
import com.wirebarley.presentation.account.dto.request.AccountCreateRequest;
import com.wirebarley.presentation.account.dto.request.DepositRequest;
import com.wirebarley.presentation.account.dto.request.TransferRequest;
import com.wirebarley.presentation.account.dto.request.WithdrawRequest;
import com.wirebarley.restdocs.docs.AccountDocs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
public class AccountDocumentTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private AccountService accountService;

    @Test
    public void createAccount() throws Exception {
        AccountDocs snippets = new AccountDocs();

        // given
        User user = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@email.com")
                .password("password")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        AccountResponse accountResponse = AccountResponse.builder()
                .id(1L)
                .accountNumber(1111L)
                .balance(100L)
                .user(UserResponse.of(user))
                .registeredAt(LocalDateTime.now())
                .unregisteredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        AccountCreateRequest request = AccountCreateRequest.builder()
                .password(1234)
                .balance(1000L)
                .userId(user.getId())
                .build();

        // when
        given(accountService.createAccount(any(), any())).willReturn(accountResponse);

        // then
        mvc.perform(
                        post("/api/v1/account")
                                .content(om.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "create-account",
                                snippets.createAccountRequestHeader(),
                                snippets.createAccountRequestBody(),
                                snippets.createAccountResponseHeader(),
                                snippets.createAccountResponseBody()
                        )
                );
    }

    @Test
    public void deleteAccount() throws Exception {
        AccountDocs snippets = new AccountDocs();

        // given
        // when
        doNothing()
                .when(accountService)
                .deleteAccount(any(), any());

        // then
        mvc.perform(
                        delete("/api/v1/account/{id}", 1)
                                .queryParam("userId", String.valueOf(1))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "delete-account",
                                snippets.deleteAccountRequestHeader(),
                                snippets.deleteAccountPathVariable(),
                                snippets.deleteAccountRequestParam(),
                                snippets.deleteAccountResponseHeader(),
                                snippets.deleteAccountResponseBody()
                        )
                );
    }

    @Test
    public void deposit() throws Exception {
        AccountDocs snippets = new AccountDocs();

        // given
        DepositRequest depositRequest = DepositRequest.builder()
                .accountNumber(1111L)
                .amount(100L)
                .sender("ATM")
                .build();

        DepositResponse depositResponse = DepositResponse.builder()
                .id(1L)
                .depositAccountNumber(1111L)
                .amount(100L)
                .depositAccountBalance(1100L)
                .type(TransactionType.DEPOSIT)
                .sender("user1")
                .receiver("ATM")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        // when
        given(accountService.deposit(any())).willReturn(depositResponse);

        // then
        mvc.perform(
                        post("/api/v1/account/deposit")
                                .content(om.writeValueAsString(depositRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "deposit",
                                snippets.depositRequestHeader(),
                                snippets.depositRequestBody(),
                                snippets.depositResponseHeader(),
                                snippets.depositResponseBody()
                        )
                );
    }

    @Test
    public void withdraw() throws Exception {
        AccountDocs snippets = new AccountDocs();

        // given
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .accountNumber(1111L)
                .amount(100L)
                .userId(1L)
                .password(1234)
                .receiver("ATM")
                .build();

        WithdrawResponse withdrawResponse = WithdrawResponse.builder()
                .id(1L)
                .withdrawAccountNumber(1111L)
                .amount(100L)
                .withdrawAccountBalance(1100L)
                .type(TransactionType.WITHDRAW)
                .sender("ATM")
                .receiver("user1")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        // when
        given(accountService.withdraw(any())).willReturn(withdrawResponse);

        // then
        mvc.perform(
                        post("/api/v1/account/withdraw")
                                .content(om.writeValueAsString(withdrawRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "withdraw",
                                snippets.withdrawRequestHeader(),
                                snippets.withdrawRequestBody(),
                                snippets.withdrawResponseHeader(),
                                snippets.withdrawResponseBody()
                        )
                );
    }

    @Test
    public void transfer() throws Exception {
        AccountDocs snippets = new AccountDocs();

        // given
        TransferRequest transferRequest = TransferRequest.builder()
                .withdrawNumber(1111L)
                .depositNumber(2222L)
                .userId(1L)
                .amount(100L)
                .accountPassword(1234)
                .build();

        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(1L)
                .withdrawAccountNumber(1111L)
                .depositAccountNumber(2222L)
                .amount(100L)
                .withdrawAccountBalance(899L)
                .depositAccountBalance(1100L)
                .type(TransactionType.TRANSFER)
                .sender("user1")
                .receiver("user2")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        // when
        given(accountService.transfer(any())).willReturn(transactionResponse);

        // then
        mvc.perform(
                        post("/api/v1/account/transfer")
                                .content(om.writeValueAsString(transferRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "transfer",
                                snippets.transferRequestHeader(),
                                snippets.transferRequestBody(),
                                snippets.transferResponseHeader(),
                                snippets.transferResponseBody()
                        )
                );
    }
}
