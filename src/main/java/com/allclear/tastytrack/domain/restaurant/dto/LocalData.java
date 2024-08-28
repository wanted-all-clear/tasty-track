package com.allclear.tastytrack.domain.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocalData { // JSON 응답 데이터를 파싱할 클래스 2

    @JsonProperty("list_total_count")
    private int listTotalCount;

    @JsonProperty("RESULT")
    private Result result;

    @JsonProperty("row")
    private List<RawRestaurantResponse> rawRestaurantResponses;

}
