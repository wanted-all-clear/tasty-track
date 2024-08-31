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

    @Query(value = "SELECT * FROM Restaurant r WHERE " +
			"r.new_address LIKE CONCAT(:regionName, '%') AND " +
			"r.type LIKE CONCAT('%', :type, '%') AND " +
			"r.status = '01' AND " +
			"r.deleted_yn = 0 AND " +
			"ST_Distance_Sphere(POINT(r.lon, r.lat), POINT(:lon, :lat)) <= :distance " +
			"ORDER BY r.rate_score DESC, r.last_updated_at DESC",
			nativeQuery = true)
	List<Restaurant> findRestaurantsWithinDistance(
			@Param("regionName") String regionName,
			@Param("lat") double lat,
			@Param("lon") double lon,
			@Param("distance") double distance,
			@Param("type") String type);

}
