package com.allclear.tastytrack.domain.restaurant.service;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantListRequest;
import com.allclear.tastytrack.domain.region.entity.Region;
import com.allclear.tastytrack.domain.region.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantByUserLocation;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.dto.UserLocationInfo;
import com.allclear.tastytrack.domain.user.enums.Coordinate;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public Restaurant getRestaurant(int id, int deletedYn) {

        Restaurant restaurant = restaurantRepository.findRestaurantById(id);
        if (restaurant == null) {
            throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
        }

        if (restaurant.getDeletedYn() == 1) {
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

        double beforeScore = restaurant.getRateScore();
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

        double northLat = userLocationInfo.getLat() + (userLocationInfo.getDistance() * Coordinate.LAT.getValue());
        double southLat = userLocationInfo.getLat() - (userLocationInfo.getDistance() * Coordinate.LAT.getValue());
        double eastLon = userLocationInfo.getLon() + (userLocationInfo.getDistance() * Coordinate.LON.getValue());
        double westLon = userLocationInfo.getLon() - (userLocationInfo.getDistance() * Coordinate.LON.getValue());

        return restaurantRepository.findBaseUserLocationByDeletedYn(westLon, eastLon, southLat, northLat);
    }

    /**
     * 멀티스레드 환경에서 비동기 방식을 이용해 List<Restaurant>를 List<RestaurantByUserLocation> 변환하는 메소드
     * - CompletableFuture를 이용하면 @async 애노테이션을 사용하지 않아도 된다.
     *
     * @param restaurants
     * @return
     */
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

    /**
     * 위치 정보에 따른 맛집 목록을 조회합니다.
     * 작성자: 배서진
     *
     * @param request
     * @return 맛집 리스트 반환
     */
    @Override
    public List<Restaurant> getRestaurantList(RestaurantListRequest request) {

        request = validateRequest(request); // 요청 객체 유효성 검사

        String type = (request.getType() != null) ? request.getType() : "";
        String name = (request.getName() != null) ? request.getName() : "";

        log.info("맛집 검색 - 위도: {}, 경도: {}, 범위: {}, 타입: {}, 이름: {}",
                request.getLat(), request.getLon(), request.getRange(), type, name);

        List<Restaurant> response = restaurantRepository.findUserRequestRestaurantList(request.getLat(), request.getLon(),
                request.getRange(), type, name);

        log.info("검색된 식당 수: {}", response.size());

        return response;
    }

    /**
     * request의 유효성 검사를 진행합니다.
     * 작성자: 배서진
     *
     * @param request 위도, 경도, 범위 숫자 유효성 검사를 진행합니다.
     * @return RestaurantsList 객체
     */
    private RestaurantListRequest validateRequest(RestaurantListRequest request) {

        log.info("요청 유효성 검사: {}", request);

        // 1. 요청 객체가 null인지 확인
        if (request == null) {
            log.error("요청이 null입니다.");
            throw new CustomException(ErrorCode.NULL_REQUEST_DATA);
        }

        // 2. 위도(lat)와 경도(lon) 유효성 검사
        if (request.getLat() < -90 || request.getLat() > 90 ||
                request.getLon() < -180 || request.getLon() > 180) {
            log.error("잘못된 위도 또는 경도. 위도: {}, 경도: {}", request.getLat(), request.getLon());
            throw new CustomException(ErrorCode.NOT_VALID_REQUEST);
        }

        // 3. 범위(range) 유효성 검사 (0 이하 값 체크)
        if (request.getRange() <= 0) {
            log.error("잘못된 범위: {}", request.getRange());
            throw new CustomException(ErrorCode.NOT_VALID_REQUEST);
        }

        log.info("요청이 유효합니다.");
        return request;
    }

    /**
     * 지역 기반의 맛집 리스트를 조회합니다.
     * 작성자: 배서진
     * - 쿼리 where절 : 도로명주소, 타입, 상태, 삭제여부
     * - 쿼리 order by절: 평점 순, 최신수정일자
     *
     * @param dosi 서울특별시
     * @param sgg  00구
     * @param type 맛집의 타입
     * @return 맛집 리스트
     */
    @Override
    public List<Restaurant> getRestaurantSearchByRegion(String dosi, String sgg, String type) {

        log.info("맛집 검색 요청 - 도/시: {}, 시/군/구: {}, 타입: {}", dosi, sgg, type);
        // 1. 입력 매개변수 유효성 검사
        if (dosi == null || sgg == null || type == null) {
            log.error("유효하지 않은 요청 - 도/시: {}, 시/군/구: {}, 타입: {}", dosi, sgg, type);
            throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
        }

        // 2. Region 조회
        Region region = regionRepository.findFirstByDosiAndSgg(dosi, sgg);
        log.info("지역 정보 조회 - 도/시: {}, 시/군/구: {}", dosi, sgg);

        if (region == null) {
            log.error("지역 정보 조회 실패 - 도/시: {}, 시/군/구: {}", dosi, sgg);
            throw new CustomException(ErrorCode.NO_REGION_DATA);
        }

        // 3. 좌표 유효성 검사
        double lat = region.getLat();
        double lon = region.getLon();
        log.info("서울특별시 지역 좌표 - 위도: {}, 경도: {}", lat, lon);


        double distance = 10.0; // 10km 반경
        String regionName = dosi + " " + sgg;

        // 4. Restaurant 조회
        log.info("맛집 조회 - 지역명: {}, 거리: {}km, 타입: {}", regionName, distance, type);
        List<Restaurant> response = restaurantRepository.findRestaurantsWithinDistance(regionName, lat, lon, distance, type);
        if (response.isEmpty()) {
            log.warn("검색된 맛집이 없습니다 - 지역명: {}, 거리: {}km, 타입: {}", regionName, distance, type);
            throw new CustomException(ErrorCode.EMPTY_RESTAURANT);
        }

        log.info("맛집 조회 완료 - 검색된 맛집 수: {}", response.size());
        return response;
    }


}
