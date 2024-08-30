package com.allclear.tastytrack.restaurant;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantServiceImpl;
import com.allclear.tastytrack.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @InjectMocks
    private RestaurantServiceImpl restaurantServiceImpl;

    @BeforeEach
    public void setUp() {

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
                .build();
        given(restaurantRepository.findRestaurantByIdAndDeletedYn(anyInt(), anyBoolean())).willReturn(restaurant);

        // when
        restaurantServiceImpl.getRestaurant(anyInt(), anyBoolean());

        //then
        verify(restaurantRepository, times(1)).findRestaurantByIdAndDeletedYn(anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("맛집 조회의 실패 테스트 입니다.")
    public void getRestaurantDetailFailTest() {
        // when
        Throwable ex = assertThrows(RuntimeException.class,
                () -> restaurantServiceImpl.getRestaurant(anyInt(), anyBoolean()));

        // then
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.NOT_VALID_PROPERTY.getMessage());
    }

    @Test
    @DisplayName("맛집 평점 업데이트 기능 성공 테스트입니다.")
    public void updateRestaurantScore() {
        // given
        Double beforeScore = 4.0;
        int beforeReviewCount = 200;
        int score = 3;

        // when
        Double result = restaurantServiceImpl.updateRestaurantScore(beforeScore, beforeReviewCount, score);

        // then
        assertThat(result).isEqualTo(((beforeScore * beforeReviewCount) + score) / (beforeReviewCount + 1));

    }


}
