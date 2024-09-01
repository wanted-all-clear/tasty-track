package com.allclear.tastytrack.domain.review.controller;

import java.util.ArrayList;
import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    public final RestaurantService restaurantService;
    public final ReviewService reviewService;

    @Operation(summary = "리뷰 생성", description = "리뷰를 생성하면서 해당 음식점의 평점을 업데이트하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰가 성공적으로 생성되었습니다.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "입력 값을 확인해주세요."),
            @ApiResponse(responseCode = "404", description = "가입되지 않은 아이디입니다.")
    })
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
                reviewResponses = reviewService.createListReviewResponse(restaurant, reviews, reviewResponses);
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
