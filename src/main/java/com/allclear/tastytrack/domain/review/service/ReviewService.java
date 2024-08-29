package com.allclear.tastytrack.domain.review.service;

import java.util.List;

import com.allclear.tastytrack.domain.review.entity.Review;

public interface ReviewService {
	public List<Review> getAllReviewsByRestaurantId(int restaurantId);
}
