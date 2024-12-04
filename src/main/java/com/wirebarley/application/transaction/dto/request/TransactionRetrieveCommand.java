package com.wirebarley.application.transaction.dto.request;

import com.wirebarley.domain.transaction.TransactionType;
import com.wirebarley.domain.transaction.dto.TransactionRetrieveQuery;
import lombok.Builder;

@Builder
public record TransactionRetrieveCommand(
        int offset,
        int count,
        Long userId,
        Long accountId,
        TransactionType type
) {

    public TransactionRetrieveQuery toQuery() {
        return TransactionRetrieveQuery.builder()
                .offset(this.offset)
                .count(this.count)
                .accountId(this.accountId)
                .type(this.type)
                .build();
    }
}
