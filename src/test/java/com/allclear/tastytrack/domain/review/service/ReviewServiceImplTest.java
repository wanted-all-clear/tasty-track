package com.allclear.tastytrack.domain.review.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.entity.User;
import com.allclear.tastytrack.domain.user.repository.UserRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

@ExtendWith({MockitoExtension.class})
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;

    @Test
    @DisplayName("리뷰 조회 테스트를 진행합니다.")
    public void getAllReviewsTest() {
        // given
        List<Review> reviews = new ArrayList<>();
        given(reviewRepository.findAllByRestaurantIdOrderByCreatedAtDesc(anyInt())).willReturn(Optional.of(reviews));

        // when
        reviewServiceImpl.getAllReviewsByRestaurantId(anyInt());

        // then
        verify(reviewRepository, times(1)).findAllByRestaurantIdOrderByCreatedAtDesc(anyInt());
    }

    @Test
    @DisplayName("리뷰 생성 해피 테스트를 진행합니다.")
    public void createReviewSuccessTest() {
        // given
        Review review = mock(Review.class);
        given(reviewRepository.save(any())).willReturn(review);
        given(userRepository.findByUsername(anyString())).willReturn(Optional.ofNullable(mock(User.class)));

        // when
        reviewServiceImpl.createReview(mock(ReviewRequest.class), anyString());

        // then
        verify(reviewRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByUsername(anyString());

    }

    @Test
    @DisplayName("리뷰 생성 실패 테스트를 진행합니다.")
    public void createReviewFailTest() {

        // given, when
        Throwable ex = assertThrows(CustomException.class,
                () -> reviewServiceImpl.createReview(mock(ReviewRequest.class), anyString()));

        // then
        verify(reviewRepository, times(0)).save(any());
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.USER_NOT_EXIST.getMessage());
    }

}
