package com.allclear.tastytrack.domain.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;

import io.lettuce.core.dynamic.annotation.Param;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    Restaurant findRestaurantById(int id);

    Restaurant findByCode(String code);

    Restaurant findByIdAndDeletedYn(int id, int deletedYn);

    @Query(nativeQuery = true,
            value = "SELECT *"
                    + "from restaurant as r"
                    + "where r.deleted_yn = 1 "
                    + "and westLon <= r.lon <= eastLon"
                    + "and southLat <= r.lat <= northLat")
    List<Restaurant> findBaseUserLocationByDeletedYn(@Param("westLon") double westLon, @Param("eastLon") double eastLon,
            @Param("southLat") double southLat, @Param("northLat") double northLat);

}
