package com.allclear.tastytrack.domain.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RawRestaurantResponse { // JSON 응답 데이터를 파싱할 클래스 4

    @JsonProperty("MGTNO")
    private String MGTNO;

    @JsonProperty("DTLSTATEGBN")
    private String DTLSTATEGBN;

    @JsonProperty("BPLCNM")
    private String BPLCNM;

    @JsonProperty("UPTAENM")
    private String UPTAENM;

    @JsonProperty("DCBYMD")
    private String DCBYMD;

    @JsonProperty("SITEPOSTNO")
    private String SITEPOSTNO;

    @JsonProperty("SITEWHLADDR")
    private String SITEWHLADDR;

    @JsonProperty("RDNWHLADDR")
    private String RDNWHLADDR;

    @JsonProperty("RDNPOSTNO")
    private String RDNPOSTNO;

    @JsonProperty("LASTMODTS")
    private String LASTMODTS;

    @JsonProperty("X")
    private String X;

    @JsonProperty("Y")
    private String Y;

}
