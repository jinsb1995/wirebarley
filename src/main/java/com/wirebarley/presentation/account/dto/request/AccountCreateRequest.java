package com.wirebarley.presentation.account.dto.request;

import com.wirebarley.application.account.dto.request.AccountCreateCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AccountCreateRequest(

        @NotNull(message = "계좌 비밀번호는 필수입니다.")
        Integer password,

        Long balance,

        @NotNull(message = "회원 정보는 필수입니다.")
        Long userId
) {

    public AccountCreateCommand toCommand() {
        return AccountCreateCommand.builder()
                .password(this.password)
                .balance(this.balance)
                .userId(this.userId)
                .build();
    }
}
