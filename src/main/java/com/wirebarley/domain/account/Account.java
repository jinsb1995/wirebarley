package com.wirebarley.domain.account;

import com.wirebarley.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Account {

    private Long id;
    private Long accountNumber;
    private Integer password;
    private Long balance;
    private User user;
    private LocalDateTime registeredAt;   // 계좌 등록일
    private LocalDateTime unregisteredAt; // 계좌 해지일
    private LocalDateTime createdAt;      // 계좌 생성일
    private LocalDateTime modifiedAt;     // 최종 수정일

    @Builder
    public Account(Long id, Long accountNumber, Integer password, Long balance, User user, LocalDateTime registeredAt, LocalDateTime unregisteredAt, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.registeredAt = registeredAt;
        this.unregisteredAt = unregisteredAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
