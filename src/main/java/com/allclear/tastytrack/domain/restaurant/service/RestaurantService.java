package com.allclear.tastytrack.domain.restaurant.service;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;

public interface RestaurantService {

    Restaurant getRestaurant(int id, boolean deletedYn);

    Double updateRestaurantScore(double beforeScore, int beforeReviewCount, int score);

}
