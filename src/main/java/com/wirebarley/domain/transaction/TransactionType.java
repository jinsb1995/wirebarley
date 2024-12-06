package com.wirebarley.domain.transaction;

import com.wirebarley.infrastructure.exception.CustomException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum TransactionType {
    WITHDRAW, // 출금
    DEPOSIT,  // 입금
    TRANSFER, // 이체
    ALL       // 입출금내역
    ;

    public static List<TransactionType> getWithdrawalTypes() {
        return List.of(WITHDRAW, TRANSFER);
    }

    public static TransactionType of(String type) {
        return Arrays.stream(TransactionType.values())
                .filter(it -> it.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new CustomException("올바른 타입을 입력해주세요."));
    }
}
