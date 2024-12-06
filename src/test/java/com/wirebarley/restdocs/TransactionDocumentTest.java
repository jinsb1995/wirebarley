package com.wirebarley.restdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirebarley.application.transaction.TransactionService;
import com.wirebarley.application.transaction.dto.response.TransactionResponse;
import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.restdocs.docs.TransactionDocs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
public class TransactionDocumentTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    public void retrieveUsers() throws Exception {
        TransactionDocs snippets = new TransactionDocs();

        // given
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
        given(transactionService.retrieveTransaction(any())).willReturn(List.of(transactionResponse));

        // then
        mvc.perform(
                        get("/api/v1/transactions")
                                .param("offset", String.valueOf(0))
                                .param("count", String.valueOf(10))
                                .param("userId", String.valueOf(1L))
                                .param("accountId", String.valueOf(1111L))
                                .param("type", TransactionType.ALL.name())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "retrieve-transactions",
                                snippets.retrieveTransactionRequestHeader(),
                                snippets.retrieveTransactionRequestParam(),
                                snippets.retrieveTransactionResponseHeader(),
                                snippets.retrieveTransactionResponseBody()
                        )
                );
    }
}
