package com.wirebarley.presentation.account.dto.request;

import jakarta.validation.constraints.NotNull;

public record AccountDeleteRequest(
        @NotNull(message = "회원 정보는 필수입니다.")
        Long userId
) {

}
