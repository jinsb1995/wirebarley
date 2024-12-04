package com.wirebarley.presentation.account.dto.request;

import com.wirebarley.application.account.dto.request.WithdrawCommand;

public record WithdrawRequest(
        Long accountNumber,
        Long amount,
        Long userId,
        Integer password,
        String receiver
) {

    public WithdrawCommand toCommand() {
        return WithdrawCommand.builder()
                .accountNumber(this.accountNumber)
                .amount(this.amount)
                .userId(this.userId)
                .password(this.password)
                .receiver(this.receiver)
                .build();
    }
}
