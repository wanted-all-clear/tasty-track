package com.allclear.tastytrack.domain.review.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

	private ReviewRepository reviewRepository;

	@Override
	public List<Review> getAllReviews(int restaurantId) {
		return reviewRepository.findAllByRestaurantIdOrderByCreatedAtDesc(restaurantId);
	}
}
