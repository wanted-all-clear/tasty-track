package com.allclear.tastytrack.domain.review.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;

public interface ReviewService {

    List<Review> getAllReviewsByRestaurantId(int restaurantId);

    List<CompletableFuture<ReviewResponse>> createReviewResponse(List<Review> reviews);

    CompletableFuture<List<ReviewResponse>> combineToListFuture(
            List<CompletableFuture<ReviewResponse>> listCompletableFuture);

    public Review createReview(ReviewRequest request);

    int getBeforeReviewTotalScore(int restaurantId);

}
