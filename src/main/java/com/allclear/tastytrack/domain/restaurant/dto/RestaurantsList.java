package com.allclear.tastytrack.domain.restaurant.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantsList {
    private double lat;
    private double lon;
    private double range; // km 단위로 요청 받음
    private String type;
    private String name;
}
