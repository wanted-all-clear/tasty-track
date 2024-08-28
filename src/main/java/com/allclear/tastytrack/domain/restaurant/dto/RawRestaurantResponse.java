package com.allclear.tastytrack.domain.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RawRestaurantResponse { // JSON 응답 데이터를 파싱할 클래스 4

    @JsonProperty("MGTNO")
    private String mgtno;

    @JsonProperty("DTLSTATEGBN")
    private String dtlstategbn;

    @JsonProperty("BPLCNM")
    private String bplcnm;

    @JsonProperty("UPTAENM")
    private String uptaenm;

    @JsonProperty("DCBYMD")
    private String dcbymd;

    @JsonProperty("SITEPOSTNO")
    private String sitepostno;

    @JsonProperty("SITEWHLADDR")
    private String sitewhladdr;

    @JsonProperty("RDNWHLADDR")
    private String rdnwhladdr;

    @JsonProperty("RDNPOSTNO")
    private String rdnpostno;

    @JsonProperty("LASTMODTS")
    private String lastmodts;

    @JsonProperty("X")
    private String lon; // 경도

    @JsonProperty("Y")
    private String lat; // 위도

}
