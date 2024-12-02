package com.wirebarley.infrastructure.exception;

import lombok.Getter;

@Getter
public enum ExceptionConstant {

    USER_NOT_FOUND_EXCEPTION("USER_NOT_FOUND", "유저를 찾을 수 없습니다.");

    private final String code;
    private final String message;

    ExceptionConstant(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
