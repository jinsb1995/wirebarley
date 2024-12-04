package com.wirebarley.presentation.account.dto.request;

import com.wirebarley.application.account.dto.request.DepositCommand;
import jakarta.validation.constraints.NotNull;

public record DepositRequest(

        @NotNull(message = "계좌번호는 필수값입니다.")
        Long accountNumber,

        @NotNull(message = "입금액은 필수값입니다.")
        Long amount,

        @NotNull
        String sender
) {

    public DepositCommand toCommand() {
        return DepositCommand.builder()
                .accountNumber(this.accountNumber)
                .amount(this.amount)
                .sender(this.sender)
                .build();
    }
}
