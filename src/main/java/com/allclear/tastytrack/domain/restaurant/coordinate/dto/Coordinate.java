package com.allclear.tastytrack.domain.restaurant.coordinate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
public class Coordinate {
    private String lon; // 경도
    private String lat; // 위도

    public Coordinate(String lon, String lat) {
        this.lon = lon;
        this.lat = lat;

    }

}
