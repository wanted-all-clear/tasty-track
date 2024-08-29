package com.allclear.tastytrack.domain.restaurant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

	private final RestaurantService restaurantService;

	public ResponseEntity<RestaurantDetail> getRestaurant() {

		return null;
	}

}
