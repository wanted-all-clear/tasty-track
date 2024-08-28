package com.allclear.tastytrack.domain.restraunt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalDataResponse { // JSON 응답 데이터를 파싱할 클래스 1

    @JsonProperty("LOCALDATA_072404")
    private LocalData localData;

}
