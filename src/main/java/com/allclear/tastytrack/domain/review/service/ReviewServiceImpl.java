package com.allclear.tastytrack.domain.review.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    /**
     * 맛집 id에 해당하는 리뷰를 전부 조회합니다.
     * 작성자 : 김은정
     * @param restaurantId
     * @return List<Review>
     */
    @Override
    public List<Review> getAllReviewsByRestaurantId(int restaurantId) {

        Optional<List<Review>> reviewsOptional =
                reviewRepository.findAllByRestaurantIdOrderByCreatedAtDesc(restaurantId);

        List<Review> reviews = reviewsOptional.orElseGet(ArrayList::new);

        log.info("조회된 리뷰의 수는 {} 입니다.", reviews.size());

        return reviews;

    }

    /**
     * 멀티스레드 환경에서 비동기 방식을 이용해 List<reviews>를 List<ReviewResponse>으로 변환하는 메소드
     *  - CompletableFuture를 이용하면 @async 애노테이션을 사용하지 않아도 된다.
     *  - 파라미터로 전달된 reviewResponses는 비어있다.
     *
     * @param restaurant
     * @param reviews
     * @param reviewResponses
     * @return
     */
    @Override
    @Transactional
    public List<ReviewResponse> createListReviewResponse(Restaurant restaurant, List<Review> reviews,
            List<ReviewResponse> reviewResponses) {

        List<CompletableFuture<ReviewResponse>> listCompletableFuture = changeListCompletableReview(reviews);
        CompletableFuture<List<ReviewResponse>> completableFuture = changeCompletableListReview(listCompletableFuture);
        reviewResponses = completableFuture.join();
        Collections.sort(reviewResponses);

        return reviewResponses;
    }

    /**
     * List<Review>를 List<CompletableFuture<ReviewResponse>>로 변경하는 메서드입니다.
     * 작성자 : 김은정
     * @param reviews
     * @return
     */
    private List<CompletableFuture<ReviewResponse>> changeListCompletableReview(List<Review> reviews) {

        List<CompletableFuture<ReviewResponse>> reviewResponses = new ArrayList<>();
        for (Review review : reviews) {
            reviewResponses.add(CompletableFuture.supplyAsync(() -> asyncCreateReviewResponse(review)));
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
    private ReviewResponse asyncCreateReviewResponse(Review review) {

        Optional<User> userOpt = userRepository.findById(review.getUser().getId());
        User user = userOpt.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        log.info("비동기 메서드로 조회하는 {}님의 리뷰입니다 : {}", user.getUsername(), user.getId());

        return ReviewResponse.builder()
                .username(user.getUsername())
                .score(review.getScore())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();

    }

    /**
     * List<CompletableFuture<ReviewResponse>>를 CompletableFuture<List<ReviewResponse>>로 변경해주는 비동기 메서드입니다.
     * 작성자 : 김은정
     * @param listCompletableFuture
     * @return
     */
    private CompletableFuture<List<ReviewResponse>> changeCompletableListReview(
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
     * 리뷰를 생성하는 메소드입니다.
     * 작성자 : 김은정
     * @return
     */
    @Transactional
    public Review createReview(ReviewRequest request, String username) {

        if (request == null) {
            log.error("조회 가능한 데이터가 존재하지 않습니다.");
            throw new CustomException(ErrorCode.NOT_VALID_PROPERTY);
        }

        try {
            User user = userRepository.findByUsername(username).get();
            Review review = Review.builder()
                    .user(user)
                    .restaurant(restaurantRepository.getReferenceById(request.getRestaurantId()))
                    .score(request.getScore())
                    .content(request.getContent())
                    .build();

            log.info("{}님이 리뷰를 작성했습니다.", user.getUsername());

            return reviewRepository.save(review);

        } catch (NoSuchElementException ex) {
            log.error("가입 되지 않은 유저 입니다.");
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }

    }

    /**
     * 리뷰 생성 api 도중에 오류가 발생했을 때 생성했던 리뷰를 삭제하기 위해 구현
     *
     * @param review
     */
    @Override
    public void removeReview(Review review) {

        log.info("{}님의 리뷰를 삭제합니다.", review.getUser().getUsername());
        reviewRepository.delete(review);
    }

}
