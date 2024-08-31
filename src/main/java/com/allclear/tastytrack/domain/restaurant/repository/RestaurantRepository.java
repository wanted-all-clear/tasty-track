package com.allclear.tastytrack.domain.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    Restaurant findRestaurantById(int id);

    Restaurant findByCode(String code);

}
