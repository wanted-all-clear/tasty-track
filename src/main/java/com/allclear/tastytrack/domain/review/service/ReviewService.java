package com.allclear.tastytrack.domain.review.service;

import java.util.List;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;

public interface ReviewService {

    List<Review> getAllReviewsByRestaurantId(int restaurantId);

    Review createReview(ReviewRequest request, String username);

    void removeReview(Review review);

    List<ReviewResponse> createListReviewResponse(Restaurant restaurant, List<Review> reviews,
            List<ReviewResponse> reviewResponses);

}
