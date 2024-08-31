package com.allclear.tastytrack.domain.restaurant.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.allclear.tastytrack.domain.review.dto.ReviewResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class RestaurantDetail {

    private String name;


    private String type;

    private String status;

    private Double rateScore;

    private String oldAddress;

    private String newAddress;

    private Double lon;

    private Double lat;

    private LocalDateTime lastUpdateAt;

    @Setter
    private List<ReviewResponse> reviewResponses;

}
