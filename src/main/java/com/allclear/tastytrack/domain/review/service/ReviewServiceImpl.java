package com.allclear.tastytrack.domain.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private ReviewRepository reviewRepository;
	private UserRepository userRepository;

	@Override
	public List<Review> getAllReviewsByRestaurantId(int restaurantId) {

		return reviewRepository.findAllByRestaurantIdOrderByCreatedAtDesc(restaurantId);
	}

	@Override
	public List<CompletableFuture<ReviewResponse>> createReviewResponse(List<Review> reviews) {

		List<CompletableFuture<ReviewResponse>> reviewResponses = new ArrayList<>();
		for (Review review : reviews) {
			reviewResponses.add(asyncCreateReviewResponse(review));
		}
		return reviewResponses;
	}

	/**
	 * Review 객체를 이용해 ReviewResponse를 생성하는 비동기 메소드 입니다.
	 * 작성자 : 김은정
	 *
	 * @param review
	 * @return
	 */
	private CompletableFuture<ReviewResponse> asyncCreateReviewResponse(Review review) {
		User user = userRepository.findById(review.getUserId()).get();

		return ReviewResponse.builder()
				.username(review.get)
	}

}
