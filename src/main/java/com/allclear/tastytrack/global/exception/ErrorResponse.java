package com.allclear.tastytrack.global.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    public String message; // 예외 메세지
    public HttpStatus httpStatus; // Http 상태 값 400, 404, 500 등

    public ErrorResponse() {

    }

    public ErrorResponse(HttpStatus status, String message) {

        this.httpStatus = status;
        this.message = message;
    }

    static public ErrorResponse create() {

        return new ErrorResponse();
    }

    public ErrorResponse message(String message) {

        this.message = message;
        return this;
    }

    public ErrorResponse httpStatus(HttpStatus httpStatus) {

        this.httpStatus = httpStatus;
        return this;
    }

}
