package com.allclear.tastytrack.domain.restaurant.repository;

import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawRestaurantRepository extends JpaRepository<RawRestaurant, Integer> {

    RawRestaurant findByMgtno(String mgtno);

}
