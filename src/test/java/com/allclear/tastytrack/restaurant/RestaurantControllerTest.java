package com.allclear.tastytrack.restaurant;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.allclear.tastytrack.domain.auth.token.JwtTokenUtils;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

        HttpEntity<String> entity = new HttpEntity<>("4", httpHeaders);
        String url = "/api/restaurants";

        ResponseEntity<RestaurantDetail> responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, entity,
                RestaurantDetail.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getName()).isEqualTo("오고보");

    }

}
