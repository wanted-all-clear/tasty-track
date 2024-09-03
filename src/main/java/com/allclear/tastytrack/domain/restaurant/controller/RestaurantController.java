package com.allclear.tastytrack.domain.restaurant.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allclear.tastytrack.domain.auth.UserDetailsImpl;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantListRequest;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantService;
import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.service.ReviewService;
import com.allclear.tastytrack.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final UserService userService;

    @Operation(summary = "음식점의 상세정보 조회", description = "특정 음식점의 상세정보를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 음식점의 상세정보가 조회되었습니다.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "입력값을 확인해주세요."),
            @ApiResponse(responseCode = "404", description = "조회할 수 없는 음식점입니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDetail> getRestaurantById(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable int id) {

        userService.getUserInfo(userDetails.getUsername());
        Restaurant restaurant = restaurantService.getRestaurantById(id, 0);
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
    @GetMapping("/list")
    public ResponseEntity<List<Restaurant>> getRestaurantList(@RequestParam(name = "lat") double lat, @RequestParam(name = "lon") double lon,
                                                              @RequestParam(name = "range") double range, @RequestParam(name = "type") String type, @RequestParam(name = "name") String name) {

        List<Restaurant> response = restaurantService.getRestaurantList(lat, lon, range, type, name);

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
