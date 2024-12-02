package com.wirebarley.application.account.dto.response;

import com.wirebarley.application.user.dto.response.UserResponse;
import com.wirebarley.domain.account.Account;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccountResponse {

    private final Long id;
    private final Long accountNumber;
    private final Long balance;
    private final UserResponse user;
    private final LocalDateTime registeredAt;   // 계좌 등록일
    private final LocalDateTime unregisteredAt; // 계좌 해지일
    private final LocalDateTime createdAt;      // 계좌 생성일
    private final LocalDateTime modifiedAt;     // 최종 수정일

    @Builder
    public AccountResponse(Long id, Long accountNumber, Long balance, UserResponse user, LocalDateTime registeredAt, LocalDateTime unregisteredAt, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.user = user;
        this.registeredAt = registeredAt;
        this.unregisteredAt = unregisteredAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static AccountResponse of(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .user(UserResponse.of(account.getUser()))
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnregisteredAt())
                .createdAt(account.getCreatedAt())
                .modifiedAt(account.getModifiedAt())
                .build();
    }
}
