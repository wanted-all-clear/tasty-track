package com.allclear.tastytrack.domain.restaurant.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.allclear.tastytrack.domain.region.entity.Region;
import com.allclear.tastytrack.domain.region.repository.RegionRepository;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantListRequest;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.review.repository.ReviewRepository;
import com.allclear.tastytrack.domain.user.dto.UserLocationInfo;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private RestaurantServiceImpl restaurantServiceImpl;
    @Mock
    private RegionRepository regionRepository;

    private Restaurant mockRestaurant;  // 전역 변수로 mockRestaurant 선언


    @BeforeEach
    public void setUp() {

        // 전역 변수로 mockRestaurant 초기화
        mockRestaurant = Restaurant.builder()
                .id(133)
                .code("3170000-101-2023-00100")
                .name("소팔소곱창 독산점")
                .type("한식")
                .status("01")
                .oldAddress("서울특별시 금천구 독산동 291-1 현대지식산업센터")
                .newAddress("서울특별시 금천구 두산로 70, 현대지식산업센터 지하1층 T-L 132호 (독산동)")
                .lon(126.895620657193)
                .lat(37.4689627800606)
                .rateScore(0.0)
                .deletedYn(0)
                .build();
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
                .deletedYn(0)
                .build();
        given(restaurantRepository.findByIdAndDeletedYn(anyInt(), anyInt())).willReturn(restaurant);

        // when
        restaurantServiceImpl.getRestaurantById(anyInt(), anyInt());

        //then
        verify(restaurantRepository, times(1)).findByIdAndDeletedYn(anyInt(), anyInt());
    }

    @Test
    @DisplayName("맛집 조회의 실패 테스트 입니다.")
    public void getRestaurantDetailFailTest() {
        // when
        Throwable ex = assertThrows(RuntimeException.class,
                () -> restaurantServiceImpl.getRestaurantById(anyInt(), anyInt()));

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
        given(restaurant.getDeletedYn()).willReturn(0);

        // when
        Restaurant result = restaurantServiceImpl.updateRestaurantScore(mock(ReviewRequest.class));

        // then
        verify(restaurantRepository, times(1)).getReferenceById(anyInt());
        verify(reviewRepository, times(1)).countByRestaurantId(anyInt());
        verify(restaurantRepository, times(1)).save(restaurant);

    }

    @Test
    @DisplayName("회원 위치 기반으로 음식점을 조회합니다.")
    public void findRestaurantByUserLocation() {
        // given
        List<Restaurant> mockList = mock(List.class);
        given(restaurantRepository.findBaseUserLocationByDeletedYn(anyDouble(), anyDouble(), anyDouble(),
                anyDouble())).willReturn(mockList);

        // when
        restaurantServiceImpl.getRestaurantByUserLocation(mock(UserLocationInfo.class));

        // then
        verify(restaurantRepository, times(1)).findBaseUserLocationByDeletedYn(anyDouble(),
                anyDouble(), anyDouble(), anyDouble());

    }

    @Test
    @DisplayName("유효한 요청으로 맛집 목록을 성공적으로 조회합니다.")
    void testGetRestaurantListWithValidRequest() {
        // given
        RestaurantListRequest request = new RestaurantListRequest(37.5088, 126.8913, 10.0, "한식", "점");

        // Mock 설정
        when(restaurantRepository.findUserRequestRestaurantList(anyDouble(), anyDouble(), anyDouble(), anyString(),
                anyString()))
                .thenReturn(List.of(mockRestaurant));

        // when
        List<Restaurant> result = restaurantServiceImpl.getRestaurantList(request);

        // then
        assertThat(result).isNotNull();
        verify(restaurantRepository, times(1)).findUserRequestRestaurantList(
                request.getLat(), request.getLon(), request.getRange(), request.getType(), request.getName());
    }

    @Test
    @DisplayName("유효하지 않은 요청으로 인해 예외가 발생합니다.")
    void testGetRestaurantListWithInvalidRequest() {
        // given
        RestaurantListRequest invalidRequest = new RestaurantListRequest(-100.0, 200.0, -5.0, "한식", "점");

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> restaurantServiceImpl.getRestaurantList(invalidRequest));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_VALID_REQUEST);
    }

    @Test
    @DisplayName("유효한 요청이지만 검색된 맛집이 없는 경우 빈 리스트를 반환합니다.")
    void testGetRestaurantListWithEmptyResult() {
        // given
        RestaurantListRequest request = new RestaurantListRequest(37.5088, 126.8913, 10.0, "한식", "점");

        // Mock 설정
        when(restaurantRepository.findUserRequestRestaurantList(anyDouble(), anyDouble(), anyDouble(), anyString(),
                anyString()))
                .thenReturn(Collections.emptyList());

        // when
        List<Restaurant> result = restaurantServiceImpl.getRestaurantList(request);

        // then
        assertThat(result).isNotNull();
        verify(restaurantRepository, times(1)).findUserRequestRestaurantList(
                request.getLat(), request.getLon(), request.getRange(), request.getType(), request.getName());
    }

    @Test
    @DisplayName("유효성 검사에서 null 요청이 들어오면 예외를 발생시킵니다.")
    void testValidateRequestWithNullRequest() {
        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> restaurantServiceImpl.getRestaurantList(null));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NULL_REQUEST_DATA);
    }

    @Test
    @DisplayName("유효성 검사에서 범위가 0 이하일 때 예외를 발생시킵니다.")
    void testValidateRequestWithInvalidRange() {
        // given
        RestaurantListRequest invalidRequest = new RestaurantListRequest(37.5088, 126.8913, 0.0, "한식", "점");

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> restaurantServiceImpl.getRestaurantList(invalidRequest));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_VALID_REQUEST);
    }


    @Test
    @DisplayName("유효한 지역 정보를 기반으로 맛집 목록을 조회합니다.")
    void testGetRestaurantSearchByRegion_Success() {
        // given
        Region region = Region.builder()
                .dosi("서울특별시")
                .sgg("금천구")
                .lat(37.44910833)
                .lon(126.9041972)
                .build();


        given(regionRepository.findFirstByDosiAndSgg(anyString(), anyString()))
                .willReturn(region);

        given(restaurantRepository.findRestaurantsWithinDistance(anyString(), anyDouble(), anyDouble(), anyDouble(),
                anyString()))
                .willReturn(List.of(mockRestaurant));

        // when
        List<Restaurant> result = restaurantServiceImpl.getRestaurantSearchByRegion("서울특별시", "금천구", "한식");

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(0).getName()).isEqualTo("소팔소곱창 독산점");
    }

    @Test
    @DisplayName("존재하지 않는 지역일 경우 예외를 발생시킵니다.")
    void testGetRestaurantSearchByRegion_NoRegionData() {
        // given
        given(regionRepository.findFirstByDosiAndSgg(anyString(), anyString()))
                .willReturn(null);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> restaurantServiceImpl.getRestaurantSearchByRegion("서울특별시", "존재하지않는구", "한식"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_REGION_DATA);
    }

    @Test
    @DisplayName("조회된 맛집이 없을 경우 예외를 발생시킵니다.")
    void testGetRestaurantSearchByRegion_EmptyRestaurantList() {
        // given
        Region region = Region.builder()
                .dosi("서울특별시")
                .sgg("금천구")
                .lat(37.44910833)
                .lon(126.9041972)
                .build();


        given(regionRepository.findFirstByDosiAndSgg(anyString(), anyString()))
                .willReturn(region);

        given(restaurantRepository.findRestaurantsWithinDistance(anyString(), anyDouble(), anyDouble(), anyDouble(),
                anyString()))
                .willReturn(Collections.emptyList());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> restaurantServiceImpl.getRestaurantSearchByRegion("서울특별시", "금천구", "한식"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMPTY_RESTAURANT);
    }

    @Test
    @DisplayName("입력 매개변수가 null일 경우 예외를 발생시킵니다.")
    void testGetRestaurantSearchByRegion_InvalidParameter() {
        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> restaurantServiceImpl.getRestaurantSearchByRegion(null, "금천구", "한식"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_VALID_PROPERTY);

        exception = assertThrows(CustomException.class,
                () -> restaurantServiceImpl.getRestaurantSearchByRegion("서울특별시", null, "한식"));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_VALID_PROPERTY);

        exception = assertThrows(CustomException.class,
                () -> restaurantServiceImpl.getRestaurantSearchByRegion("서울특별시", "금천구", null));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_VALID_PROPERTY);
    }

}
