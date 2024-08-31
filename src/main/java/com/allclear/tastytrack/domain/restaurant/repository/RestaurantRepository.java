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


     /** [조건절] - 영업상태가 01, 폐업되어 row 삭제 여부 0, type명, 사업장명
     *   [정렬] - 거리 가까운 순, 평점 높은 순, 최신 수정 일자 내림차순 으로 정렬
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
    List<Restaurant> findUserRequestRestaurant(@Param("lat") double lat,
                                               @Param("lon") double lon,
                                               @Param("range") double range,
                                               @Param("type") String type,
                                               @Param("name") String name);


}
