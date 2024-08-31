package com.allclear.tastytrack.domain.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantByUserLocationRequest {

    private String restaurantName;
    private Double rateScore;

}
