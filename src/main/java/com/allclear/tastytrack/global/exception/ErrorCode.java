package com.allclear.tastytrack.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // USER
    USERNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "중복된 계정명입니다."),

    USER_NOT_EXIST(HttpStatus.NOT_FOUND, "가입되지 않은 아이디입니다."),
    NOT_VALID_PROPERTY(HttpStatus.BAD_REQUEST, "입력 값을 확인해주세요."),
    UNKNOWN_USER_POSITION(HttpStatus.BAD_REQUEST, "사용자의 위치를 알 수 없습니다."),
    USER_NOT_ELIGIBLE(HttpStatus.BAD_REQUEST, "회원정보 수정에서 점심추천 메뉴 서비스를 설정해주세요."),

    // Restaurant
    NOT_EXISTENT_RESTAURANT(HttpStatus.NOT_FOUND, "조회할 수 없는 음식점입니다."),
    CANNOT_LEAVE_REVIEW(HttpStatus.BAD_REQUEST, "해당 음식점에 평점을 남길 수 없습니다."),
    EMPTY_RESTAURANT(HttpStatus.NO_CONTENT, "맛집 데이터가 0건 입니다."),

    NO_NEARBY_RESTAURANTS(HttpStatus.NOT_FOUND, "인근에 조회할 수 있는 음식점이 없습니다."),

    // Region
    NO_REGION_DATA(HttpStatus.NOT_FOUND, "지역 정보 데이터가 없습니다."),
    DATABASE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다."),
    NOT_VALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다."),
    NULL_REQUEST_DATA(HttpStatus.BAD_REQUEST, "요청한 값이 없습니다."),

    // DISCORD
    INVALID_DISCORD_MESSAGE(HttpStatus.BAD_REQUEST, "메시지 전송 중 오류가 발생했습니다."),
    ENCODING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "검색어 인코딩에 실패하였습니다."),
    // Data Pipeline
    API_NOT_FOUND(HttpStatus.NOT_FOUND, "API 요청에 실패했습니다."),
    JSON_PARSING(HttpStatus.BAD_REQUEST, "JSON 파싱 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {

        this.httpStatus = httpStatus;
        this.message = message;
    }
}
