package com.allclear.tastytrack.domain.restaurant.service;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;

public interface RestaurantService {

    Restaurant getRestaurant(int id, int deletedYn);

    Restaurant updateRestaurantScore(ReviewRequest request);

}
