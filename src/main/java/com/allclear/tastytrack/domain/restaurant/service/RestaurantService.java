package com.allclear.tastytrack.domain.restaurant.service;

import java.util.List;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.entity.Review;

public interface RestaurantService {
	public Restaurant getRestaurant(Long id);
	public List<Review> getAllReviews(Long restaurantId);
}
