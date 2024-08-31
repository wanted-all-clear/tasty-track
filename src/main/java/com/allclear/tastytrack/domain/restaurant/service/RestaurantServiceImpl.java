package com.allclear.tastytrack.domain.restaurant.service;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantByUserLocation;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.dto.UserLocationInfo;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public Restaurant getRestaurant(int id, int deletedYn) {

        Restaurant restaurant = restaurantRepository.findByIdAndDeletedYn(id, deletedYn);
        if (restaurant == null) {
            throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
        }

        if (restaurant.getDeletedYn() == 0) {
            throw new CustomException(ErrorCode.NOT_EXISTENT_RESTAURANT);
        }

        return restaurant;
    }

    @Override
    @Transactional
    public Restaurant updateRestaurantScore(ReviewRequest request) {

        Restaurant restaurant = restaurantRepository.getReferenceById(request.getRestaurantId());

        if (restaurant.getDeletedYn() == 0) {
            throw new CustomException(ErrorCode.NOT_EXISTENT_RESTAURANT);
        }

        int countReview = reviewRepository.countByRestaurantId(request.getRestaurantId());
        int score = request.getScore();

        double newScore = (restaurant.getRateScore() * (countReview - 1) + score) / countReview;
        double newScoreFormat = Math.floor((newScore * 10)) / 10.0;
        restaurant.setRateScore(newScoreFormat);

        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> getRestaurantByUserLocation(UserLocationInfo userLocationInfo) {

        if (userLocationInfo == null) {
            throw new CustomException(ErrorCode.UNKNOWN_USER_POSITION);
        }

        double nothLat = userLocationInfo.getNothLat();
        double southLat = userLocationInfo.getSouthLat();
        double eastLon = userLocationInfo.getEastLon();
        double westLon = userLocationInfo.getWestLon();

        return restaurantRepository.findBaseUserLocationByDeletedYn(westLon, eastLon, southLat, nothLat);
    }

    @Override
    public List<RestaurantByUserLocation> createListRestaurantByUserLocation(List<Restaurant> restaurants) {

        if (restaurants.isEmpty()) {
            throw new CustomException(ErrorCode.NO_NEARBY_RESTAURANTS);
        }

        List<CompletableFuture<RestaurantByUserLocation>> listComplResult = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {

            // RestaurantByUserLocation 변환 작업이 CompletableFuture에 의해 비동기로 실행되었기 때문에 ComplatableFuture로 감싸진다.
            listComplResult.add(CompletableFuture
                    .supplyAsync(() -> changeListComplRestaurantByUserLocation(restaurant))
            );
        }

        CompletableFuture<List<RestaurantByUserLocation>> complListRestaurantByUserLocation =
                changeComplListRestaurantByUserLocation(listComplResult);


        return complListRestaurantByUserLocation.join();
    }

    /**
     * // 멀티 스레드를 이용하여 Restaurant 객체를 RestaurantByUserLocation 객체로 변환하는 비동기 메소드
     * @param restaurant
     * @return
     */
    private RestaurantByUserLocation changeListComplRestaurantByUserLocation(
            Restaurant restaurant) {

        return RestaurantByUserLocation.builder()
                .restaurantName(restaurant.getName())
                .rateScore(restaurant.getRateScore())
                .build();
    }

    /**
     * 필요한 데이터의 형태인 List<RestaurantByUserLocation>로 변환해주는 비동기 메서드
     * @param listComplResult
     * @return
     */
    private CompletableFuture<List<RestaurantByUserLocation>> changeComplListRestaurantByUserLocation(
            List<CompletableFuture<RestaurantByUserLocation>> listComplResult) {

        // 1. listComplResult을 배열로 변환해준다.
        CompletableFuture<?>[] complArray = listComplResult.toArray(new CompletableFuture<?>[0]);

        // 2. allOf() 메소드를 이용해 비동기로 전달된 데이터가 전부 도착할 때까지 기다린 뒤 List 타입으로 다시 변환해준다.
        return CompletableFuture.allOf(complArray)
                .thenApplyAsync(i ->
                        listComplResult.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()));
    }

}
