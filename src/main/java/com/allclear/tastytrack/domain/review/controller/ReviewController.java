package com.allclear.tastytrack.domain.review.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allclear.tastytrack.domain.auth.UserDetailsImpl;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantService;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.service.ReviewService;
import com.allclear.tastytrack.global.exception.CustomException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    public final RestaurantService restaurantService;
    @Autowired
    public final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<RestaurantDetail> createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ReviewRequest request) {

        Review review = null;
        try {
            review = reviewService.createReview(request, userDetails.getUsername());
            Restaurant restaurant = restaurantService.updateRestaurantScore(request);
            List<Review> reviews = reviewService.getAllReviewsByRestaurantId(request.getRestaurantId());

            List<ReviewResponse> reviewResponses = new ArrayList<>();
            if (!reviews.isEmpty()) {

                List<CompletableFuture<ReviewResponse>> listCompletableFuture =
                        reviewService.createReviewResponse(reviews);

                CompletableFuture<List<ReviewResponse>> completableFuture =
                        reviewService.combineToListFuture(listCompletableFuture);

                reviewResponses = completableFuture.join();
                Collections.sort(reviewResponses);

            }
            RestaurantDetail restaurantDetail = RestaurantDetail.builder()
                    .name(restaurant.getName())
                    .type(restaurant.getType())
                    .status(restaurant.getStatus())
                    .rateScore(restaurant.getRateScore())
                    .oldAddress(restaurant.getOldAddress())
                    .newAddress(restaurant.getNewAddress())
                    .lon(restaurant.getLon())
                    .lat(restaurant.getLat())
                    .lastUpdateAt(restaurant.getLastUpdatedAt())
                    .reviewResponses(reviewResponses)
                    .build();

            return ResponseEntity.ok(restaurantDetail);

        } catch (CustomException ex) {
            reviewService.removeReview(review);
            throw ex;
        }
    }

}
