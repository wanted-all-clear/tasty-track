package com.allclear.tastytrack.domain.restaurant.repository;

import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RawRestaurantRepository extends JpaRepository<RawRestaurant, Integer> {

    // 원본 맛집과 가공 맛집의 최종수정일자가 다른 원본 맛집 테이블의 데이터 조회
    @Query(value = "SELECT raw.* FROM raw_restaurant raw " +
            "LEFT JOIN restaurant r ON raw.mgtno = r.code " +
            "WHERE STR_TO_DATE(raw.lastmodts, '%Y-%m-%d %H:%i:%s') != r.last_updated_at", nativeQuery = true)
    List<RawRestaurant> findByLastUpdatedAtNotMatchingRawRestaurants();

    RawRestaurant findByMgtno(String mgtno);

}
