package com.allclear.tastytrack.domain.restaurant.service;

import java.util.List;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantByUserLocation;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.user.dto.UserLocationInfo;

public interface RestaurantService {

    Restaurant getRestaurant(int id, int deletedYn);

    Restaurant updateRestaurantScore(ReviewRequest request);

    List<Restaurant> getRestaurantByUserLocation(UserLocationInfo userLocationInfo);

    List<RestaurantByUserLocation> createListRestaurantByUserLocation(List<Restaurant> restaurants);

}
