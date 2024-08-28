package com.allclear.tastytrack.domain.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allclear.tastytrack.domain.restaurant.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findAllByRestaurantId(Long restaurantId);
}
