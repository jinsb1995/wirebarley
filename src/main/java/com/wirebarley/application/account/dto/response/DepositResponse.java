package com.wirebarley.application.account.dto.response;

import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DepositResponse {

    private final Long id;
    private final Long depositAccountNumber;
    private final Long amount;
    private final Long depositAccountBalance;
    private final TransactionType type;
    private final String sender;
    private final String receiver;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    private DepositResponse(Long id, Long depositAccountNumber, Long amount, Long depositAccountBalance, TransactionType type, String sender, String receiver, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.depositAccountNumber = depositAccountNumber;
        this.amount = amount;
        this.depositAccountBalance = depositAccountBalance;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static DepositResponse of(Transaction transaction) {
        return DepositResponse.builder()
                .id(transaction.getId())
                .depositAccountNumber(transaction.getDepositAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .depositAccountBalance(transaction.getDepositAccountBalance())
                .type(transaction.getType())
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .createdAt(transaction.getCreatedAt())
                .modifiedAt(transaction.getModifiedAt())
                .build();
    }
}
