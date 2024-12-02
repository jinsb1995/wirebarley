package com.wirebarley.infrastructure.account.entity;

import com.wirebarley.domain.account.Account;
import com.wirebarley.infrastructure.common.entity.BaseEntity;
import com.wirebarley.infrastructure.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "account")
@Entity
public class AccountEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountNumber;

    private Integer password;

    private Long balance;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    private LocalDateTime registeredAt;   // 계좌 등록일

    private LocalDateTime unregisteredAt; // 계좌 해지일

    @Builder
    private AccountEntity(Long accountNumber, Integer password, Long balance, UserEntity user, LocalDateTime registeredAt, LocalDateTime unregisteredAt) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.registeredAt = registeredAt;
        this.unregisteredAt = unregisteredAt;
    }

    public static AccountEntity create(Account account, UserEntity userEntity) {
        return AccountEntity.builder()
                .accountNumber(account.getAccountNumber())
                .password(account.getPassword())
                .balance(account.getBalance())
                .user(userEntity)
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnregisteredAt())
                .build();
    }

    public Account toDomain() {
        return Account.builder()
                .id(this.id)
                .accountNumber(this.accountNumber)
                .password(this.password)
                .balance(this.balance)
                .user(this.user.toDomain())
                .registeredAt(this.registeredAt)
                .unregisteredAt(this.unregisteredAt)
                .createdAt(this.getCreatedAt())
                .modifiedAt(this.getModifiedAt())
                .build();
    }
}
