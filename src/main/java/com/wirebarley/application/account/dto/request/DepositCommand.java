package com.wirebarley.application.account.dto.request;

import lombok.Builder;

@Builder
public record DepositCommand(
        Long accountNumber,
        Long amount,
        String sender
) {

}
