package com.wirebarley.infrastructure.transaction.entity;

import com.wirebarley.domain.transaction.Transaction;
import com.wirebarley.domain.transaction.TransactionType;
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

    private Long withdrawAccountNumber;

    private Long depositAccountNumber;

    private Long amount;

    private Long withdrawAccountBalance;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String sender;

    private String receiver;

    @Builder
    private TransactionEntity(Long id, Long withdrawAccountNumber, Long depositAccountNumber, Long amount, Long withdrawAccountBalance, TransactionType type, String sender, String receiver) {
        this.id = id;
        this.withdrawAccountNumber = withdrawAccountNumber;
        this.depositAccountNumber = depositAccountNumber;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    public static TransactionEntity create(Transaction transaction) {

        return TransactionEntity.builder()
                .withdrawAccountNumber(transaction.getWithdrawAccountNumber())
                .depositAccountNumber(transaction.getDepositAccountNumber())
                .amount(transaction.getAmount())
                .withdrawAccountBalance(transaction.getWithdrawAccountBalance())
                .type(transaction.getType())
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .build();
    }

    public Transaction toDomain() {
        return Transaction.builder()
                .id(this.id)
                .withdrawAccountNumber(this.withdrawAccountNumber)
                .depositAccountNumber(this.depositAccountNumber)
                .amount(this.amount)
                .withdrawAccountBalance(this.withdrawAccountBalance)
                .type(this.type)
                .sender(this.sender)
                .receiver(this.receiver)
                .build();
    }
}
