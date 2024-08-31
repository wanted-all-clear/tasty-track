package com.allclear.tastytrack.domain.restaurant.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.dto.UserLocationInfo;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public Restaurant getRestaurant(int id, int deletedYn) {

        Restaurant restaurant = restaurantRepository.findByIdAndDeletedYn(id, deletedYn);
        if (restaurant == null) {
            throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
        }

        if (restaurant.getDeletedYn() == 0) {
            throw new CustomException(ErrorCode.NOT_EXISTENT_RESTAURANT);
        }

        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant updateRestaurantScore(ReviewRequest request) {

        Restaurant restaurant = restaurantRepository.getReferenceById(request.getRestaurantId());

        if (restaurant.getDeletedYn() == 0) {
            throw new CustomException(ErrorCode.NOT_EXISTENT_RESTAURANT);
        }

        double beforeScore = restaurant.getRateScore();
        int countReview = reviewRepository.countByRestaurantId(request.getRestaurantId());
        int score = request.getScore();

        double newScore = (restaurant.getRateScore() * (countReview - 1) + score) / countReview;
        double newScoreFormat = Math.floor((newScore * 10)) / 10.0;
        restaurant.setRateScore(newScoreFormat);

        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> getRestaurantByUserLocation(UserLocationInfo userLocationInfo) {

        if (userLocationInfo == null) {
            throw new CustomException(ErrorCode.UNKNOWN_USER_POSITION);
        }

        double nothLat = userLocationInfo.getNothLat();
        double southLat = userLocationInfo.getSouthLat();
        double eastLon = userLocationInfo.getEastLon();
        double westLon = userLocationInfo.getWestLon();

        return restaurantRepository.findBaseUserLocationByDeletedYn(westLon, eastLon, southLat, nothLat);
    }

}
