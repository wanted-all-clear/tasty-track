package com.allclear.tastytrack.domain.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;

	@Override
	public List<Review> getAllReviewsByRestaurantId(int restaurantId) {

		return reviewRepository.findAllByRestaurantIdOrderByCreatedAtDesc(restaurantId).get();
	}

	@Override
	public List<CompletableFuture<ReviewResponse>> createReviewResponse(List<Review> reviews) {

		List<CompletableFuture<ReviewResponse>> reviewResponses = new ArrayList<>();
		for (Review review : reviews) {
			reviewResponses.add(CompletableFuture.supplyAsync(() -> asyncCreateReviewResponse(review)));
		}
		return reviewResponses;
	}

	@Override
	public CompletableFuture<List<ReviewResponse>> combineToListFuture(
			List<CompletableFuture<ReviewResponse>> listCompletableFuture) {

		CompletableFuture<?>[] completableFutureArray =
				listCompletableFuture.toArray(new CompletableFuture<?>[0]);

		return CompletableFuture.allOf(completableFutureArray)
				.thenApplyAsync(
						i -> listCompletableFuture.stream()
								.map(CompletableFuture::join)
								.collect(Collectors.toList()));
	}

	/**
	 * Review 객체를 이용해 ReviewResponse를 생성하는 비동기 메소드 입니다.
	 * 작성자 : 김은정
	 *
	 * @param review
	 * @return
	 */
	private ReviewResponse asyncCreateReviewResponse(Review review) {

		User user = userRepository.findById(review.getUserId()).get();

		return ReviewResponse.builder()
				.username(user.getUsername())
				.score(review.getScore())
				.content(review.getContent())
				.build();

	}


}
