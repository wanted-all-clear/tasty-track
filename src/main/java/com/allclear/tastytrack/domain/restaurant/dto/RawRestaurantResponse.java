package com.allclear.tastytrack.domain.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RawRestaurantResponse { // JSON 응답 데이터를 파싱할 클래스 4

    @JsonProperty("MGTNO")
    private String mgtno;       // 관리번호

    @JsonProperty("DTLSTATEGBN")
    private String dtlstategbn; // 상세영업코드

    @JsonProperty("BPLCNM")
    private String bplcnm;      // 사업장명

    @JsonProperty("UPTAENM")
    private String uptaenm;     // 업태구분명

    @JsonProperty("DCBYMD")
    private String dcbymd;      // 폐업일자

    @JsonProperty("SITEWHLADDR")
    private String sitewhladdr; // 지번주소

    @JsonProperty("RDNWHLADDR")
    private String rdnwhladdr;  // 도로명주소

    @JsonProperty("LASTMODTS")
    private String lastmodts;   // 최종 수정일자

    @JsonProperty("X")
    private String lon;         // 경도

    @JsonProperty("Y")
    private String lat;         // 위도

}
