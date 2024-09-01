package com.allclear.tastytrack.domain.restaurant.controller;

import java.util.ArrayList;
import java.util.List;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantListRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.allclear.tastytrack.domain.auth.UserDetailsImpl;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantService;
import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@Tag(name = "restaurant", description = "맛집 관련 API")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<RestaurantDetail> getRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody int id) {

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

    @Operation(summary = "맛집 목록 조회", description = "제공된 필터 조건에 따라 맛집 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "맛집 목록을 성공적으로 조회하였습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Restaurant.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    @PostMapping("/list")
    public ResponseEntity<List<Restaurant>> getRestaurantList(@RequestBody RestaurantListRequest request) {

        List<Restaurant> response = restaurantService.getRestaurantList(request);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/region")
    public ResponseEntity<List<Restaurant>> getRestuarantSearchByRegion(@RequestParam String dosi,
                                                                        @RequestParam String sgg,
                                                                        @RequestParam String type) {
        // 특정 지역의 맛집을 검색하여 반환
        List<Restaurant> response = restaurantService.getRestaurantSearchByRegion(dosi, sgg, type);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }


}
