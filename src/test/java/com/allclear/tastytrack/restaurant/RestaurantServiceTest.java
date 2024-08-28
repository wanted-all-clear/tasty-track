package com.allclear.tastytrack.restaurant;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.allclear.tastytrack.domain.address.repository.AddressRepository;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.repository.ReviewRepository;
import com.allclear.tastytrack.domain.restaurant.service.RestaurantServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

	@Mock
	private RestaurantRepository restaurantRepository;
	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private AddressRepository addressRepository;

	@InjectMocks
	private RestaurantServiceImpl restaurantServiceImpl;

	@BeforeEach
	public void setUp() {

	}

	/**
	 * 맛집 조회의 해피 테스트 입니다.
	 * 작성자 : 김은정
	 */
	@Test
	public void getRestaurantDetailSuccessTest() {
		//given
		Restaurant restaurant = Restaurant.builder()
			.name("맥도날드")
			.addressId(101L)
			.code("1234-8945")
			.type("패스트푸드")
			.status("영업")
			.rateScore(4.2)
			.build();
		given(restaurantRepository.findById(101L)).willReturn(Optional.ofNullable(restaurant));

		// when
		restaurantServiceImpl.getRestaurant(any());

		//then
		verify(restaurantRepository, times(1)).findById(any());
		verify(reviewRepository, times(1)).findAllByRestaurantId(any());
		verify(addressRepository, times(1)).findById(any());
	}

	/**
	 * 맛집 조회의 실패 테스트 입니다.
	 * - 맛집을 조회하지 못 하는 경우
	 * 작성자 : 김은정
	 */
	@Test
	public void getRestaurantDetailFailTest() {
		// when
		Throwable ex = assertThrows(RuntimeException.class, () -> restaurantServiceImpl.getRestaurant(any()));

		// then
		assertThat(ex.getMessage()).isEqualTo("조회된 레스토랑이 없습니다.");
	}
}
