package com.allclear.tastytrack.domain.restaurant.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RestaurantDetail {
	private String restaurantName;
	private String dosi;
	private String sgg;
	private String zipCode;
	private String restaurantCode;
	private String type;
	private String status;
	private Double rateScore;
	private List<ReviewResponse> reviewResponses;
}
