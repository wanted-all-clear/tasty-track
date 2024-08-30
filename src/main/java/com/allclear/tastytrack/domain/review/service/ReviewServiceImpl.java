package com.allclear.tastytrack.domain.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * 맛집 id에 해당하는 리뷰를 전부 조회합니다.
     * 작성자 : 김은정
     * @param restaurantId
     * @return List<Review>
     */
    @Override
    public List<Review> getAllReviewsByRestaurantId(int restaurantId) {

        return reviewRepository.findAllByRestaurantIdOrderByCreatedAtDesc(restaurantId).get();
    }

    /**
     * List<Review>를 List<CompletableFuture<ReviewResponse>>로 변경하는 메서드입니다.
     * 작성자 : 김은정
     * @param reviews
     * @return
     */
    @Override
    public List<CompletableFuture<ReviewResponse>> createReviewResponse(List<Review> reviews) {

        List<CompletableFuture<ReviewResponse>> reviewResponses = new ArrayList<>();
        for (Review review : reviews) {
            reviewResponses.add(CompletableFuture.supplyAsync(() -> asyncCreateReviewResponse(review)));
        }
        return reviewResponses;
    }

    /**
     * List<CompletableFuture<ReviewResponse>>를 CompletableFuture<List<ReviewResponse>>로 변경해주는 비동기 메서드입니다.
     * 작성자 : 김은정
     * @param listCompletableFuture
     * @return
     */
    @Override
    public CompletableFuture<List<ReviewResponse>> combineToListFuture(
            List<CompletableFuture<ReviewResponse>> listCompletableFuture) {

        CompletableFuture<?>[] completableFutureArray =
                listCompletableFuture.toArray(new CompletableFuture<?>[0]);

        // allOf() 메소드를 통해 비동기 작업이 끝날 때까지 기다린다.
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


    /**
     * 리뷰를 생성하는 메소드입니다.
     * 작성자 : 김은정
     * @return
     */
    public Review createReview(ReviewRequest request) {

        if (request == null) {
            throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
        }

        Review review = Review.builder()
                .userId(request.getUserid())
                .restaurantId(request.getRestaurantId())
                .score(request.getScore())
                .content(request.getContent())
                .build();

        return reviewRepository.save(review);

    }

    @Override
    public int getBeforeReviewTotalScore(int restaurantId) {


        return 0;
    }

}
