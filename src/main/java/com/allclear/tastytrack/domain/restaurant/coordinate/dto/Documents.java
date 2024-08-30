package com.allclear.tastytrack.domain.restaurant.coordinate.dto;

import lombok.Getter;

@Getter
public class Documents {
    private CoordinateAddress address;
    private String address_name;
    private String address_type;
    private CoordinateRoadAddress road_address;
    private String x;
    private String y;

}
