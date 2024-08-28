package com.allclear.tastytrack.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // USER
    USERNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "중복된 계정명입니다."),

    USER_NOT_EXIST(HttpStatus.NOT_FOUND, "가입되지 않은 아이디입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {

        this.httpStatus = httpStatus;
        this.message = message;
    }
}
