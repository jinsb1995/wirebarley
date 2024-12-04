package com.wirebarley.domain.transaction.dto;

import com.wirebarley.domain.transaction.TransactionType;
import lombok.Builder;

@Builder
public record TransactionRetrieveQuery(
        int offset,
        int count,
        Long accountId,
        TransactionType type
) {
}
