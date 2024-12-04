package com.wirebarley.infrastructure.exception;

import lombok.Getter;

@Getter
public enum ExceptionConstant {

    USER_NOT_FOUND_EXCEPTION("USER_NOT_FOUND", "유저를 찾을 수 없습니다."),
    NOT_OWNER_EXCEPTION("NOT_OWNER", "계좌 소유자가 아닙니다."),
    ACCOUNT_NOT_FOUND_EXCEPTION("ACCOUNT_NOT_FOUND", "계좌가 존재하지 않습니다."),
    NOT_OWNER_ACCOUNT_EXCEPTION("NOT_OWNER_ACCOUNT", "계좌 소유자가 아닙니다."),
    WRONG_ACCOUNT_PASSWORD_EXCEPTION("WRONG_ACCOUNT_PASSWORD", "계좌 비밀번호 검증에 실패했습니다."),
    NOT_ENOUGH_BALANCE_EXCEPTION("NOT_ENOUGH_BALANCE", "계좌 잔액이 부족합니다."),
    SAME_ACCOUNT_EXCEPTION("SAME_ACCOUNT", "출금계좌와 입금계좌는 동일할 수 없습니다."),
    ZERO_AMOUNT_EXCEPTION("ZERO_AMOUNT", "0원 이하의 금액을 이체할 수 없습니다."),
    
    DAILY_LIMIT_EXCEPTION("DAILY_LIMIT", "일 이체 한도를 초과했습니다."),
    WEEKLY_LIMIT_EXCEPTION("WEEKLY_LIMIT", "주 이체 한도를 초과했습니다."),
    MONTHLY_LIMIT_EXCEPTION("MONTHLY_LIMIT", "월 이체 한도를 초과했습니다."),

    ;

    private final String code;
    private final String message;

    ExceptionConstant(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
