package com.wirebarley.presentation.transaction.dto.request;

import com.wirebarley.application.transaction.dto.request.TransactionRetrieveCommand;
import com.wirebarley.domain.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TransactionRetrieveRequest(

        Integer offset,

        Integer count,

        @NotNull(message = "계좌 소유주 정보는 필수입니다.")
        Long userId,

        @NotNull(message = "계좌번호는 필수입니다.")
        Long accountId,

        String type
) {
    public TransactionRetrieveRequest {
        if (offset == null) {
            offset = 0;
        }

        if (count == null) {
            count = 10;
        }

        if (type == null) {
            type = TransactionType.ALL.name();
        }
    }

    public TransactionRetrieveCommand toCommand() {
        return TransactionRetrieveCommand.builder()
                .offset(this.offset)
                .count(this.count)
                .userId(this.userId)
                .accountId(this.accountId)
                .type(TransactionType.of(this.type))
                .build();
    }
}
