package com.allclear.tastytrack.domain.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    Restaurant findRestaurantById(int id);

    Restaurant findByCode(String code);

    Restaurant findByIdAndDeletedYn(int id, int deletedYn);

    List<Restaurant> findBaseUserLocationByDeletedYn(double minLon, double minLat, double maxLon, double maxLat,
            int deletedYn);

}
