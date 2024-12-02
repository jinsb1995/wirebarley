package com.wirebarley.infrastructure.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiResponse<T> {

    private final int code;
    private final HttpStatus status;
    private final String message;
    private final T data;

    public ApiResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "success", data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.CREATED, "success", data), HttpStatus.CREATED);
    }

    public static <T> ApiResponse<T> fail(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }
}
