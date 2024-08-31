package com.allclear.tastytrack.domain.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSearch {
    private String dosi;
    private String sgg;
    private String type;

}
