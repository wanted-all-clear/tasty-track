package com.allclear.tastytrack.domain.restaurant.coordinate.dto;

import lombok.Getter;

@Getter
public class Documents { // 주소 관련 데이터 반환 객체
    private CoordinateAddress address;
    private String address_name;
    private String address_type;
    private CoordinateRoadAddress road_address;
    private String x;
    private String y;

}
