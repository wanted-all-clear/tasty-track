package com.allclear.tastytrack.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allclear.tastytrack.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Optional<List<Review>> findAllByRestaurantIdOrderByCreatedAtDesc(Integer restaurantId);

    int countByRestaurantId(int restaurantId);

}
