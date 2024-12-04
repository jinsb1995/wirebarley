package com.wirebarley.infrastructure.transaction.entity;

import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.infrastructure.account.entity.AccountEntity;
import com.wirebarley.infrastructure.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "transactions")
@Entity
public class TransactionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity withdrawAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity depositAccount;

    private Long amount;

    private Long withdrawAccountBalance;

    private Long depositAccountBalance;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String sender;

    private String receiver;

    @Builder
    private TransactionEntity(Long id, AccountEntity withdrawAccount, AccountEntity depositAccount, Long amount, Long withdrawAccountBalance, Long depositAccountBalance, TransactionType type, String sender, String receiver) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.depositAccountBalance = depositAccountBalance;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    public static TransactionEntity create(Transaction transaction) {

        AccountEntity withdrawAccount = transaction.getWithdrawAccount() != null ? AccountEntity.create(transaction.getWithdrawAccount()) : null;
        AccountEntity depositAccount = transaction.getDepositAccount() != null ? AccountEntity.create(transaction.getDepositAccount()) : null;
        return TransactionEntity.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .amount(transaction.getAmount())
                .withdrawAccountBalance(transaction.getWithdrawAccountBalance())
                .depositAccountBalance(transaction.getDepositAccountBalance())
                .type(transaction.getType())
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .build();
    }

    public Transaction toDomain() {
        Account withdrawAccount = this.withdrawAccount != null ? this.withdrawAccount.toDomain() : null;
        Account depositAccount = this.depositAccount != null ? this.depositAccount.toDomain() : null;
        return Transaction.builder()
                .id(this.id)
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .amount(this.amount)
                .withdrawAccountBalance(this.withdrawAccountBalance)
                .depositAccountBalance(this.depositAccountBalance)
                .type(this.type)
                .sender(this.sender)
                .receiver(this.receiver)
                .build();
    }
}
