package com.allclear.tastytrack.domain.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService{
	private RestaurantRepository restaurantRepository;
	private ReviewRepository reviewRepository;

	@Override
	public Restaurant getRestaurant(Long id) {
		Optional<Restaurant> restaurantOpt = restaurantRepository.findById(id);
		if(restaurantOpt.isEmpty()) {
			throw new RuntimeException("조회된 레스토랑이 없습니다.");
		}

		return restaurantOpt.get();
	}

	@Override
	public List<Review> getAllReviews(Long restaurantId) {
		return reviewRepository.findAllByRestaurantIdOrderByCreatedAtDesc(restaurantId);
	}
}
