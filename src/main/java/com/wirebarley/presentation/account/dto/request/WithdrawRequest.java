package com.wirebarley.presentation.account.dto.request;

import com.wirebarley.application.account.dto.request.WithdrawCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WithdrawRequest(

        @NotNull(message = "출금 계좌번호는 필수값입니다.")
        Long accountNumber,

        @NotNull(message = "출금액은 필수값입니다.")
        Long amount,

        @NotNull(message = "유저 정보는 필수값입니다.")
        Long userId,
        
        @NotNull(message = "비밀번호는 필수값입니다.")
        Integer password,
        
        @NotNull(message = "받는 사람을 입력해주세요.")
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
