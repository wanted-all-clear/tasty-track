package com.allclear.tastytrack.domain.restaurant.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantService;
import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    @Operation(summary = "음식점의 상세정보 조회", description = "특정 음식점의 상세정보를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 음식점의 상세정보가 조회되었습니다.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "입력값을 확인해주세요."),
            @ApiResponse(responseCode = "404", description = "조회할 수 없는 음식점입니다.")
    })
    @PostMapping("")
    public ResponseEntity<RestaurantDetail> getRestaurant(@RequestBody int id) {

        Restaurant restaurant = restaurantService.getRestaurant(id, 0);
        List<Review> reviews = reviewService.getAllReviewsByRestaurantId(id);

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
    }

}
