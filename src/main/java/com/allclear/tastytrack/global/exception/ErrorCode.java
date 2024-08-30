package com.allclear.tastytrack.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// USER
	USERNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "중복된 계정명입니다."),

	USER_NOT_EXIST(HttpStatus.NOT_FOUND, "가입되지 않은 아이디입니다."),

	NOT_VALID_PROPERTY(HttpStatus.BAD_REQUEST, "입력 값을 확인해주세요"),
	NOT_EXISTENT_RESTAURANT(HttpStatus.BAD_REQUEST, "조회할 수 없는 음식점입니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {

		this.httpStatus = httpStatus;
		this.message = message;
	}
}
