package com.wirebarley.presentation.account.dto.request;

import com.wirebarley.application.account.dto.request.TransferCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransferRequest(

        @NotNull(message = "출금 계좌번호는 필수입니다.")
        Long withdrawNumber,

        @NotNull(message = "입금 계좌번호는 필수입니다.")
        Long depositNumber,

        @NotNull(message = "계좌 소유주 정보는 필수입니다.")
        Long userId,

        @NotNull(message = "이체 금액은 필수입니다.")
        Long amount,

        @NotNull(message = "출금계좌 비밀번호는 필수입니다.")
        Integer accountPassword,

        LocalDateTime transferDate
) {

    public TransferCommand toCommand() {
        return TransferCommand.builder()
                .withdrawNumber(this.withdrawNumber)
                .depositNumber(this.depositNumber)
                .userId(this.userId)
                .amount(this.amount)
                .accountPassword(this.accountPassword)
                .transferDate(this.transferDate)
                .build();
    }
}
