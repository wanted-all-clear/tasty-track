package com.allclear.tastytrack.domain.restaurant.service;


import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public Restaurant getRestaurant(int id, boolean deletedYn) {

        Restaurant restaurant = restaurantRepository.findRestaurantByIdAndDeletedYn(id, deletedYn);
        if (restaurant == null) {
            throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
        }

        if (restaurant.isDeletedYn()) {
            throw new CustomException(ErrorCode.NOT_EXISTENT_RESTAURANT);
        }

        return restaurant;
    }

    @Override
    public Double updateRestaurantScore(double beforeScore, int beforeReviewCount, int score) {

        return ((beforeScore * beforeReviewCount) + score) / (beforeReviewCount + 1);
    }


}
