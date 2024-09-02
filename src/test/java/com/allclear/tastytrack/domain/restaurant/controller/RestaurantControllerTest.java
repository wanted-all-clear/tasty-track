package com.allclear.tastytrack.domain.restaurant.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.allclear.tastytrack.domain.auth.token.JwtTokenUtils;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantByUserLocation;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;
import com.allclear.tastytrack.domain.user.dto.UserLocationInfo;
import com.fasterxml.jackson.core.JsonProcessingException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestaurantControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    private HttpHeaders httpHeaders;


    @BeforeEach
    public void setUp() {

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("username")
                .password("qlalfqjsgh23")
                .lat(14128052.4047183)
                .lon(4526216.5022505).build();
        HttpEntity<UserCreateRequest> entity = new HttpEntity<>(userCreateRequest);
        testRestTemplate.exchange("/api/users", HttpMethod.POST, entity, String.class);

        String token = jwtTokenUtils.generateJwtToken(userCreateRequest.getUsername());
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + token);
    }

    @Test
    @DisplayName("맛집 상세 정보 조회 통합 테스트 입니다.")
    public void 맛집_상세정보_조회_테스트() throws JsonProcessingException {

        HttpEntity<Integer> entity = new HttpEntity<>(1, httpHeaders);
        String url = "/api/restaurants/" + 1;

        ResponseEntity<RestaurantDetail> responseEntity = testRestTemplate.exchange(url, HttpMethod.GET, entity,
                RestaurantDetail.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getName()).isEqualTo("매취랑 동대문점");

    }

    @Test
    @DisplayName("사용자 위치 기반 맛집조회 통합 테스트 입니다.")
    public void 사용자_위치_기반_맛집조회_테스트() {

        UserLocationInfo userLocationInfo = UserLocationInfo.builder()
                .lat(37.4987846719974)
                .lon(127.031703595662)
                .distance(1)
                .build();

        HttpEntity<UserLocationInfo> entity = new HttpEntity<>(userLocationInfo, httpHeaders);
        String url = "/api/users/location";

        ResponseEntity<List<RestaurantByUserLocation>> responseEntity
                = testRestTemplate.exchange(url, HttpMethod.POST, entity,
                new ParameterizedTypeReference<List<RestaurantByUserLocation>>() {
                });

        List<RestaurantByUserLocation> result = responseEntity.getBody();
        assertThat(result.size()).isEqualTo(11);
        assertThat(result.get(0).getRestaurantName()).isEqualTo("뮤지컬펍넘버스테이지");
    }

}
