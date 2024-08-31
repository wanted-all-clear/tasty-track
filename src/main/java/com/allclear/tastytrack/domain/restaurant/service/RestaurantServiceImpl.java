package com.allclear.tastytrack.domain.restaurant.service;


import com.allclear.tastytrack.domain.region.entity.Region;
import com.allclear.tastytrack.domain.region.repository.RegionRepository;
import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

	private final RestaurantRepository restaurantRepository;
	private final ReviewRepository reviewRepository;
	private final RegionRepository regionRepository;

	@Override
	public Restaurant getRestaurant(int id) {

		Restaurant restaurant = restaurantRepository.findRestaurantById(id);
		if (restaurant == null) {
			throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
		}

		return restaurant;
	}

	/** 지역 기반의 맛집 리스트를 조회합니다.
	 *  작성자: 배서진
	 *  - 쿼리 where절 : 도로명주소, 타입, 상태, 삭제여부
	 *  - 쿼리 order by절: 평점 순, 최신수정일자
	 *
	 * @param dosi 서울특별시
	 * @param sgg 00구
	 * @param type 맛집의 타입
	 * @return 맛집 리스트
	 */
	@Override
	public List<Restaurant> getRestaurantSearchByRegion(String dosi, String sgg, String type) {

		// 1. 입력 매개변수 유효성 검사
		if (dosi == null || sgg == null || type == null) {
			throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
		}

		// 2. Region 조회
		Region region = regionRepository.findFirstByDosiAndSgg(dosi, sgg);
		if (region == null) {
			throw new CustomException(ErrorCode.NO_REGION_DATA);
		}

		// 3. 좌표 유효성 검사
		double lat = region.getLat();
		double lon = region.getLon();

        double distance = 10000.0; // 10km 반경
		String regionName = dosi + " " + sgg;

		// 4. Restaurant 조회
		List<Restaurant> response = restaurantRepository.findRestaurantsWithinDistance(regionName, lat, lon, distance, type);
		if (response.isEmpty()) {
			throw new CustomException(ErrorCode.EMPTY_RESTAURANT);
		}

		return response;
	}






}
