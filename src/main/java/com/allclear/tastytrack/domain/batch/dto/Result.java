package com.allclear.tastytrack.domain.batch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result { // JSON 응답 데이터를 파싱할 클래스 3

    @JsonProperty("CODE")
    private String code;    // 공공데이터 요청결과 코드

    @JsonProperty("MESSAGE")
    private String message; // 공공데이터 요청결과 메시지

}
