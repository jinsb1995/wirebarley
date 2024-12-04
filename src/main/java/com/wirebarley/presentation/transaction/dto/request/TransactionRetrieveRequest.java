package com.wirebarley.presentation.transaction.dto.request;

import com.wirebarley.application.transaction.dto.request.TransactionRetrieveCommand;
import com.wirebarley.domain.transaction.TransactionType;
import lombok.Builder;

@Builder
public record TransactionRetrieveRequest(
        int offset,
        int count,
        Long userId,
        Long accountId,
        TransactionType type
) {

    public TransactionRetrieveCommand toCommand() {
        return TransactionRetrieveCommand.builder()
                .offset(this.offset)
                .count(this.count)
                .userId(this.userId)
                .accountId(this.accountId)
                .type(this.type)
                .build();
    }
}
