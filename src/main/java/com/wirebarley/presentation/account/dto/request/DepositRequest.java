package com.wirebarley.presentation.account.dto.request;

import com.wirebarley.application.account.dto.request.DepositCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DepositRequest(

        @NotNull(message = "입금 계좌번호는 필수값입니다.")
        Long accountNumber,

        @NotNull(message = "입금액은 필수값입니다.")
        Long amount,

        @NotNull(message = "보내는 사람을 입력해주세요.")
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
