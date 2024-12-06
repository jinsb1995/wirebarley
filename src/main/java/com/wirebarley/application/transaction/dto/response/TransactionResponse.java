package com.wirebarley.application.transaction.dto.response;

import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TransactionResponse {

    private final Long id;
    private final Long withdrawAccountNumber;
    private final Long depositAccountNumber;
    private final Long amount;
    private final Long withdrawAccountBalance;
    private final Long depositAccountBalance;
    private final TransactionType type;
    private final String sender;
    private final String receiver;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public TransactionResponse(Long id, Long withdrawAccountNumber, Long depositAccountNumber, Long amount, Long withdrawAccountBalance, Long depositAccountBalance, TransactionType type, String sender, String receiver, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.withdrawAccountNumber = withdrawAccountNumber;
        this.depositAccountNumber = depositAccountNumber;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.depositAccountBalance = depositAccountBalance;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static TransactionResponse of(Transaction transaction) {
        Long withdrawAccountNumber = transaction.getWithdrawAccount() != null ? transaction.getWithdrawAccount().getAccountNumber() : null;
        Long depositAccountNumber = transaction.getDepositAccount() != null ? transaction.getDepositAccount().getAccountNumber() : null;
        return TransactionResponse.builder()
                .id(transaction.getId())
                .withdrawAccountNumber(withdrawAccountNumber)
                .depositAccountNumber(depositAccountNumber)
                .amount(transaction.getAmount())
                .withdrawAccountBalance(transaction.getWithdrawAccountBalance())
                .depositAccountBalance(transaction.getDepositAccountBalance())
                .type(transaction.getType())
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .createdAt(transaction.getCreatedAt())
                .modifiedAt(transaction.getModifiedAt())
                .build();
    }
}
