package com.wirebarley.domain.account;

import com.wirebarley.domain.user.User;
import com.wirebarley.infrastructure.exception.CustomException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.wirebarley.infrastructure.exception.ExceptionConstant.*;
import static com.wirebarley.infrastructure.exception.ExceptionConstant.NOT_ENOUGH_BALANCE_EXCEPTION;

@Getter
public class Account {

    private static final double TRANSFER_CHARGE = 0.01;

    private Long id;
    private Long accountNumber;
    private Integer password;
    private Long balance;
    private User user;
    private LocalDateTime registeredAt;   // 계좌 등록일
    private LocalDateTime unregisteredAt; // 계좌 해지일
    private LocalDateTime createdAt;      // 계좌 생성일
    private LocalDateTime modifiedAt;     // 최종 수정일

    @Builder
    public Account(Long id, Long accountNumber, Integer password, Long balance, User user, LocalDateTime registeredAt, LocalDateTime unregisteredAt, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.registeredAt = registeredAt;
        this.unregisteredAt = unregisteredAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }


    // 출금 소유자 확인 (로그인한 사람 동일한지)
    public void checkOwner(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new CustomException(NOT_OWNER_EXCEPTION.getMessage());
        }
    }

    // 출금계좌 비밀번호 확인
    public void checkPassword(Integer accountPassword) {
        if (!this.password.equals(accountPassword)) {
            throw new CustomException(WRONG_ACCOUNT_PASSWORD_EXCEPTION.getMessage());
        }
    }

    public Long getTransferCharge(Long amount) {
        return Math.round(amount * TRANSFER_CHARGE);
    }

    // 출금계좌 잔액 확인
    public void checkEnoughBalanceByCharge(Long amount, Long transferCharge) {
        Long amountWithTransferCharge = amount + transferCharge;
        if (this.balance < amountWithTransferCharge) {
            throw new CustomException(NOT_ENOUGH_BALANCE_EXCEPTION.getMessage());
        }
    }

    // 출금하기
    public void withdraw(Long amount, Long transferCharge) {
        checkEnoughBalanceByCharge(amount, transferCharge);
        Long amountWithTransferCharge = amount + transferCharge;
        this.balance = this.balance - amountWithTransferCharge;
    }

    // 입금하기
    public void deposit(Long amount) {
        this.balance = this.balance + amount;
    }
}
