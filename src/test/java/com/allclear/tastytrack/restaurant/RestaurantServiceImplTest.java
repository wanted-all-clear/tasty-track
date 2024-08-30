package com.allclear.tastytrack.restaurant;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {

	@Mock
	private RestaurantRepository restaurantRepository;
	@InjectMocks
	private RestaurantServiceImpl restaurantServiceImpl;

	@DisplayName("맛집 조회의 해피 테스트 입니다.")
	@Test
	public void getRestaurantDetailSuccessTest() {
		//given
		Restaurant restaurant = Restaurant.builder()
				.name("맥도날드")
				.code("1234-8945")
				.type("패스트푸드")
				.status("영업")
				.rateScore(4.2)
				.oldAddress("서울특별시 종로구 세종로 1-68")
				.newAddress("서울특별시 종로구 세종대로 175")
				.lon(37.5665)
				.lat(126.9780)
				.build();
		given(restaurantRepository.findRestaurantById(anyInt())).willReturn(restaurant);

		// when
		restaurantServiceImpl.getRestaurant(anyInt());

		//then
		verify(restaurantRepository, times(1)).findById(anyInt());
	}

	@DisplayName("맛집 조회의 실패 테스트 입니다.")
	@Test
	public void getRestaurantDetailFailTest() {
		// when
		Throwable ex = assertThrows(RuntimeException.class, () -> restaurantServiceImpl.getRestaurant(anyInt()));

		// then
		assertThat(ex.getMessage()).isEqualTo("조회된 음식점이 존재하지 않습니다.");
	}

}
