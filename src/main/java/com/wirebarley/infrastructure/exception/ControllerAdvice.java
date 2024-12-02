package com.wirebarley.infrastructure.exception;

import com.wirebarley.infrastructure.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> bindException(BindException e) {
        return ResponseEntity.badRequest()
                .body(
                        ApiResponse.fail(
                                HttpStatus.BAD_REQUEST,
                                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> CustomException(CustomException e) {
        return ResponseEntity.badRequest()
                .body(
                        ApiResponse.fail(
                                HttpStatus.BAD_REQUEST,
                                e.getMessage(),
                                null
                        )
                );
    }
}
