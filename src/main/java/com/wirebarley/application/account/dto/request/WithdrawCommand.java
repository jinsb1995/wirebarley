package com.wirebarley.application.account.dto.request;

import lombok.Builder;

@Builder
public record WithdrawCommand(
        Long accountNumber,
        Long amount,
        Long userId,
        Integer password,
        String receiver
) {

}
