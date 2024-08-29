package com.allclear.tastytrack.reivew;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.allclear.tastytrack.domain.review.entity.Review;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.review.service.ReviewServiceImpl;

@ExtendWith({MockitoExtension.class})
public class ReviewServiceImplTest {

	@Mock
	private ReviewRepository reviewRepository;
	@InjectMocks
	private ReviewServiceImpl reviewServiceImpl;

	@Test
	@DisplayName("리뷰 조회를 테스트 합니다.")
	public void getAllReviewsTest() {
		// given
		List<Review> reviews = new ArrayList<>();
		given(reviewRepository.findAllByRestaurantIdOrderByCreatedAtDesc(anyInt())).willReturn(reviews);

		// when
		reviewServiceImpl.getAllReviewsByRestaurantId(anyInt());

		// then
		verify(reviewRepository, times(1)).findAllByRestaurantIdOrderByCreatedAtDesc(anyInt());
	}

}
