package com.wirebarley.domain.transaction;

import lombok.Getter;

@Getter
public enum TransactionType {
    WITHDRAW, // 출금
    DEPOSIT,  // 입금
    TRANSFER, // 이체
    ALL       // 입출금내역
}
