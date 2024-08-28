package com.allclear.tastytrack.global.exception.handler;

import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import com.allclear.tastytrack.global.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.allclear.tastytrack.global.exception.ErrorCode.NOT_VALID_PROPERTY;

@RestControllerAdvice
@Slf4j
public class RestApiExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {

        log.error("handleHttpRequestMethodNotSupportedException", ex);

        final ErrorResponse response
                = ErrorResponse
                .create()
                .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .message(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        ErrorResponse response = ErrorResponse.create()
                .message(ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<ErrorResponse> handleNullPointException(NullPointerException ex) {

        log.error("handleNullPointException : {}", ex.getMessage());

        ex.printStackTrace();

        ErrorResponse response = ErrorResponse.create()
                .message(ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * @Vaild 검증 실패 시 에러 처리
     */
    @ExceptionHandler(value = {BindException.class})
    protected ResponseEntity<ErrorResponse> handleBindException(BindException ex) {

        log.error("handleBindException : {}", ex.getMessage());


        String message = ex.getMessage();
        String defaultMsg = message.substring(message.lastIndexOf("[") + 1, message.lastIndexOf("]")); // "[" 또는 "]" 기준으로 메시지 추출

        ErrorResponse response = ErrorResponse.create()
                .message(defaultMsg)
                .httpStatus(HttpStatus.BAD_REQUEST);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {

        log.error("handleCustomException", ex);


        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();

        ErrorResponse response
                = ErrorResponse
                .create()
                .message(message)
                .httpStatus(errorCode.getHttpStatus());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = {PropertyValueException.class})
    protected ResponseEntity<ErrorResponse> handlePropertyValueException(PropertyValueException ex) {

        log.error("handlePropertyValueException : {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.create()
                .message(new CustomException(NOT_VALID_PROPERTY).getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST);

        return ResponseEntity.badRequest().body(response);
    }
}
