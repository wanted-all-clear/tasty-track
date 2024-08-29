package com.allclear.tastytrack.domain.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocalData { // JSON 응답 데이터를 파싱할 클래스 2

    @JsonProperty("list_total_count")
    private int listTotalCount; // 공공데이터 총 데이터 건수

    @JsonProperty("RESULT")
    private Result result;      // 공공데이터 요청결과 객체

    @JsonProperty("row")
    private List<RawRestaurantResponse> rawRestaurantResponses; // 공공데이터 맛집 응답 객체 리스트

}
