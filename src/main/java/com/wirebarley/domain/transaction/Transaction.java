package com.wirebarley.domain.transaction;

import com.wirebarley.domain.account.Account;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Transaction {

    private Long id;
    private Account withdrawAccount;
    private Account depositAccount;
    private Long amount;
    private Long withdrawAccountBalance;
    private Long depositAccountBalance;
    private TransactionType type;
    private String sender;
    private String receiver;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    private Transaction(Long id, Account withdrawAccount, Account depositAccount, Long amount, Long withdrawAccountBalance, Long depositAccountBalance, TransactionType type, String sender, String receiver, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.depositAccountBalance = depositAccountBalance;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
