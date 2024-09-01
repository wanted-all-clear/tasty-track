package com.allclear.tastytrack.restaurant;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.*;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantsList;
import com.allclear.tastytrack.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantServiceImpl;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private RestaurantServiceImpl restaurantServiceImpl;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("맛집 조회의 해피 테스트 입니다.")
    public void getRestaurantDetailSuccessTest() {
        //given
        Restaurant restaurant = Restaurant.builder()
                .name("맥도날드")
                .code("1234-8945")
                .type("패스트푸드")
                .status("영업")
                .rateScore(4.2)
                .oldAddress("old address")
                .newAddress("new address")
                .lon(3123.1231)
                .lat(134323.13212)
                .deletedYn(1)
                .build();
        given(restaurantRepository.findByIdAndDeletedYn(anyInt(), anyInt())).willReturn(restaurant);

        // when
        restaurantServiceImpl.getRestaurant(anyInt(), anyInt());

        //then
        verify(restaurantRepository, times(1)).findByIdAndDeletedYn(anyInt(), anyInt());
    }

    @Test
    @DisplayName("맛집 조회의 실패 테스트 입니다.")
    public void getRestaurantDetailFailTest() {
        // when
        Throwable ex = assertThrows(RuntimeException.class,
                () -> restaurantServiceImpl.getRestaurant(anyInt(), anyInt()));

        // then
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.NOT_VALID_PROPERTY.getMessage());
    }

    @Test
    @DisplayName("맛집 평점 업데이트 기능 성공 테스트입니다.")
    public void updateRestaurantScore() {
        // given
        Restaurant restaurant = mock(Restaurant.class);
        given(restaurantRepository.getReferenceById(anyInt())).willReturn(restaurant);
        given(reviewRepository.countByRestaurantId(anyInt())).willReturn(1);
        given(restaurantRepository.save(any())).willReturn(restaurant);
        given(restaurant.getDeletedYn()).willReturn(1);

        // when
        Restaurant result = restaurantServiceImpl.updateRestaurantScore(mock(ReviewRequest.class));

        // then
        verify(restaurantRepository, times(1)).getReferenceById(anyInt());
        verify(reviewRepository, times(1)).countByRestaurantId(anyInt());
        verify(restaurantRepository, times(1)).save(restaurant);

    }


}
