package com.wirebarley.application.account.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransferCommand(
        Long withdrawNumber,
        Long depositNumber,
        Long userId,
        Long amount,
        Integer accountPassword,
        LocalDateTime transferDate
) {

    public TransferCommand {
        if (transferDate == null) {
            transferDate = LocalDateTime.now();
        }
    }
}
