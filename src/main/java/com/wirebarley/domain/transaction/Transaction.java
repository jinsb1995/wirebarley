package com.wirebarley.domain.transaction;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Transaction {

    private Long id;
    private Long withdrawAccountNumber;
    private Long depositAccountNumber;
    private Long amount;
    private Long withdrawAccountBalance;
    private TransactionType type;
    private String sender;
    private String receiver;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    private Transaction(Long id, Long withdrawAccountNumber, Long depositAccountNumber, Long amount, Long withdrawAccountBalance, TransactionType type, String sender, String receiver, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.withdrawAccountNumber = withdrawAccountNumber;
        this.depositAccountNumber = depositAccountNumber;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
