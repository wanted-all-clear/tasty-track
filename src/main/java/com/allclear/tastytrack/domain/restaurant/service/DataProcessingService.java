package com.allclear.tastytrack.domain.restaurant.service;

import com.allclear.tastytrack.domain.restaurant.coordinate.dto.Coordinate;
import com.allclear.tastytrack.domain.restaurant.coordinate.service.CoordinateService;
import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RawRestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataProcessingService {

    private final RawRestaurantRepository rawRestaurantRepository;
    private final RestaurantRepository restaurantRepository;

    private final CoordinateService coordinateService;

    /**
     * 1. 맛집 원본 데이터 전처리 후 가공된 데이터를 맛집 가공 테이블에 저장하여 초기 데이터를 구축합니다.
     * 작성자 : 유리빛나
     */
    public void preprocessingAndSaveInitRestaurant() throws Exception {

        // 맛집 원본 리스트의 모든 데이터 조회
        List<RawRestaurant> rawRestaurantList = rawRestaurantRepository.findAll();

        // 맛집 원본 리스트를 맛집 가공 DB에 저장
        saveRestaurantsFromRawRestaurants(rawRestaurantList);
    }

    /**
     * 2. 맛집 원본과 맛집 가공의 최종수정일자가 다른 맛집 원본 데이터 전처리 후 가공된 데이터를 맛집 가공 테이블에 저장합니다.
     * 작성자 : 유리빛나
     */
    public void preprocessingAndSaveUpdatedAtNotMatchingRestaurant() throws Exception {

        // 맛집 원본과 맛집 가공의 최종수정일자가 다른 맛집 원본 테이블의 데이터 조회
        List<RawRestaurant> rawRestaurantList = rawRestaurantRepository.findByLastUpdatedAtNotMatchingRawRestaurants();

        // 업데이트된 맛집 원본 데이터가 없을 경우 리턴
        if (rawRestaurantList.isEmpty()) return;

        // 맛집 원본 리스트를 맛집 가공 DB에 저장
        saveRestaurantsFromRawRestaurants(rawRestaurantList);
    }

    /**
     * 맛집 원본 객체의 최종 수정일자를 문자열에서 LocalDateTime 타입으로 파싱하기 위한 메서드
     * 작성자 : 유리빛나
     *
     * @param lastmodts 맛집 원본 객체의 최종 수정일자를 나타내는 문자열
     * @return 형식에 맞게 파싱된 LocalDateTime 객체
     */
    private LocalDateTime parseLastmodts(String lastmodts) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(lastmodts, formatter);
    }

    /**
     * 맛집 원본 데이터를 맛집 가공 Entity에 저장하기 위해 Builder 패턴으로 Restaurant 객체를 생성하는 메서드
     * 작성자 : 유리빛나
     *
     * @param rawRestaurant 맛집 원본 데이터
     * @return 맛집 원본 데이터가 저장된 맛집 가공 Entity 객체
     */
    private Restaurant getRestaurantBuilder(RawRestaurant rawRestaurant) {

        return Restaurant.builder()
                .code(rawRestaurant.getMgtno())
                .name(rawRestaurant.getBplcnm())
                .type(rawRestaurant.getUptaenm())
                .status(rawRestaurant.getDtlstategbn())
                .oldAddress(rawRestaurant.getSitewhladdr())
                .newAddress(rawRestaurant.getRdnwhladdr())
                .lon(Double.valueOf(rawRestaurant.getLon()))
                .lat(Double.valueOf(rawRestaurant.getLat()))
                .lastUpdatedAt(parseLastmodts(rawRestaurant.getLastmodts()))
                .build();
    }

    /**
     * 맛집 원본 리스트를 맛집 가공 DB에 저장하는 메서드
     * 작성자 : 유리빛나
     *
     * @param rawRestaurantList 맛집 가공 DB에 저장할 맛집 원본 리스트
     */
    private void saveRestaurantsFromRawRestaurants(List<RawRestaurant> rawRestaurantList) throws Exception {

        for (RawRestaurant rawRestaurant : rawRestaurantList) {
            // 도로명주소가 없는 데이터는 일단 제외
            if (rawRestaurant.getRdnwhladdr().isEmpty()) continue;

            // 위도, 경도 값이 누락된 데이터 주입
            if (rawRestaurant.getLon().isEmpty()) {
                Coordinate coordinate = coordinateService.getCoordinate(rawRestaurant.getRdnwhladdr());
                rawRestaurant.setLon(coordinate.getLon());
                rawRestaurant.setLat(coordinate.getLat());
            }

            // 가공 테이블에서 동일한 mgtno(code) 값을 가진 데이터를 조회
            Restaurant existingRestaurant = restaurantRepository.findByCode(rawRestaurant.getMgtno());

            if (existingRestaurant != null) {
                // 기존 데이터가 있는 경우, 최종 수정일자를 비교하여 업데이트
                LocalDateTime existingLastUpdatedAt = existingRestaurant.getLastUpdatedAt();
                LocalDateTime newLastUpdatedAt = parseLastmodts(rawRestaurant.getLastmodts());

                if (!existingLastUpdatedAt.equals(newLastUpdatedAt)) {
                    // 최종수정일자가 다른 경우에만 업데이트
                    existingRestaurant.updateWithNewData(getRestaurantBuilder(rawRestaurant));
                    restaurantRepository.save(existingRestaurant);
                }
            } else {
                // 신규 데이터인 경우에만 저장
                Restaurant restaurant = getRestaurantBuilder(rawRestaurant);

                // 맛집 원본에 폐업일자가 있는 데이터는 맛집 가공의 삭제여부를 true로 저장
                if (!rawRestaurant.getDcbymd().isEmpty()) {
                    restaurant.setDeletedYn(true);
                }

                try {
                    // 가공된 데이터 저장
                    restaurantRepository.save(restaurant);
                } catch (DataIntegrityViolationException e) {
                    log.error("중복된 데이터로 인해 저장 실패: {}", rawRestaurant.getMgtno(), e);
                    // 중복된 데이터로 인한 예외가 발생한 경우 로깅 및 처리
                }
            }
        }
    }

}
