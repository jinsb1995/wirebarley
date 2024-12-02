package com.wirebarley.application.account.dto.request;

import com.wirebarley.domain.account.Account;
import com.wirebarley.domain.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccountCreateCommand(
        Integer password,
        Long balance,
        Long userId
) {

    public Account toDomain(User user, Long newAccountNumber, LocalDateTime registeredAt) {
        return Account.builder()
                .accountNumber(newAccountNumber)
                .password(this.password)
                .balance(this.balance)
                .user(user)
                .registeredAt(registeredAt)
                .build();
    }
}
