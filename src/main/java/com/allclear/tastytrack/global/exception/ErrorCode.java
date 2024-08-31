package com.allclear.tastytrack.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// USER
	USERNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "중복된 계정명입니다."),

	USER_NOT_EXIST(HttpStatus.NOT_FOUND, "가입되지 않은 아이디입니다."),

	NOT_VALID_PROPERTY(HttpStatus.BAD_REQUEST, "입력 값을 확인해주세요"),
	NOT_EXISTENT_RESTAURANT(HttpStatus.BAD_REQUEST, "조회할 수 없는 음식점입니다."),
	EMPTY_RESTAURANT(HttpStatus.NO_CONTENT, "맛집 데이터가 0건 입니다."),


	// Region
	NO_REGION_DATA(HttpStatus.NOT_FOUND, "지역 정보 데이터가 없습니다."),
	DATABASE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {

		this.httpStatus = httpStatus;
		this.message = message;
	}
}
