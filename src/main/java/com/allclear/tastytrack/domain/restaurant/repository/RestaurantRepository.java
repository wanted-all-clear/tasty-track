package com.allclear.tastytrack.domain.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    Restaurant findRestaurantById(int id);

    Restaurant findByCode(String code);

    Restaurant findByIdAndDeletedYn(int id, int deletedYn);


    /**
     * [조건절]
     * - new_address가 존재하면 사용하고,
     * 그렇지 않으면 old_address를 사용하여 regionName으로 시작하는 주소를 검색.
     * - type명,영업상태(status) '01', row 삭제 여부 (deleted_yn = 0), 지정된 거리(distance) 내에 있는 맛집을 조회
     * <p>
     * [정렬]
     * - 거리 가까운 순, 평점 높은 순, 최신 수정 일자 내림차순으로 정렬합니다.
     */
    @Query(value = "SELECT * FROM Restaurant r WHERE " +
            "(CASE WHEN r.new_address IS NOT NULL THEN r.new_address ELSE r.old_address END) LIKE CONCAT(:regionName, '%') AND " +
            "r.type LIKE CONCAT('%', :type, '%') AND " +
            "r.status = '01' AND " +
            "r.deleted_yn = 0 AND " +
            "ST_Distance_Sphere(POINT(r.lon, r.lat), POINT(:lon, :lat)) <= (:distance * 1000) " +
            "ORDER BY ST_Distance_Sphere(POINT(r.lon, r.lat), POINT(:lon, :lat)) ASC, " +
            "r.rate_score DESC, r.last_updated_at DESC",
            nativeQuery = true)
    List<Restaurant> findRestaurantsWithinDistance(
            @Param("regionName") String regionName,
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("distance") double distance,
            @Param("type") String type);


    /**
     * [조건절] - 영업상태가 01, 폐업되어 row 삭제 여부 0, type명, 사업장명
     * [정렬] - 거리 가까운 순, 평점 높은 순, 최신 수정 일자 내림차순 으로 정렬
     * 작성자: 배서진
     */
    @Query(value = "SELECT * FROM Restaurant r WHERE " +
            "r.status = '01' AND " +
            "r.deleted_yn = 0 AND " +
            "r.name LIKE CONCAT('%', :name, '%') AND " +
            "r.type LIKE CONCAT('%', :type, '%') AND " +
            "ST_Distance_Sphere(POINT(r.lon, r.lat), POINT(:lon, :lat)) <= (:range * 1000) " +
            "ORDER BY ST_Distance_Sphere(POINT(r.lon, r.lat), POINT(:lon, :lat)) ASC, " +
            "r.rate_score DESC, " +
            "r.last_updated_at DESC",
            nativeQuery = true)
    List<Restaurant> findUserRequestRestaurantList(@Param("lat") double lat,
                                                   @Param("lon") double lon,
                                                   @Param("range") double range,
                                                   @Param("type") String type,
                                                   @Param("name") String name);


}
