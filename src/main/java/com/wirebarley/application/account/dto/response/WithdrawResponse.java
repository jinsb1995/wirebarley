package com.wirebarley.application.account.dto.response;

import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WithdrawResponse {

    private final Long id;
    private final Long withdrawAccountNumber;
    private final Long amount;
    private final Long withdrawAccountBalance;
    private final TransactionType type;
    private final String sender;
    private final String receiver;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    private WithdrawResponse(Long id, Long withdrawAccountNumber, Long amount, Long withdrawAccountBalance, TransactionType type, String sender, String receiver, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.withdrawAccountNumber = withdrawAccountNumber;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static WithdrawResponse of(Transaction transaction) {
        return WithdrawResponse.builder()
                .id(transaction.getId())
                .withdrawAccountNumber(transaction.getWithdrawAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .withdrawAccountBalance(transaction.getWithdrawAccountBalance())
                .type(transaction.getType())
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .createdAt(transaction.getCreatedAt())
                .modifiedAt(transaction.getModifiedAt())
                .build();
    }
}
