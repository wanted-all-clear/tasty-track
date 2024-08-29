package com.allclear.tastytrack.domain.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

	private RestaurantRepository restaurantRepository;
	private ReviewRepository reviewRepository;

	@Override
	public Restaurant getRestaurant(int id) {

		Restaurant restaurant = restaurantRepository.findById(id);
		if (restaurant == null) {
			throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
		}

		return restaurant;
	}

}
