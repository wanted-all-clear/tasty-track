package com.allclear.tastytrack.domain.restaurant.coordinate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
public class Coordinate {
    private String lon;
    private String lat;

    public Coordinate(String lon, String lat) {
        this.lon = lon;
        this.lat = lat;

    }

}
