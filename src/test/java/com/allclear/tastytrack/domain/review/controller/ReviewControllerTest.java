package com.allclear.tastytrack.domain.review.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.ActiveProfiles;

import com.allclear.tastytrack.domain.auth.token.JwtTokenUtils;
import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;
import com.allclear.tastytrack.domain.review.dto.ReviewRequest;
import com.allclear.tastytrack.domain.user.dto.UserCreateRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ReviewControllerTest {

    @Autowired
    public TestRestTemplate testRestTemplate;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    private HttpHeaders httpHeaders;
    private UserCreateRequest userCreateRequest;

    @BeforeEach
    public void setUp() {

        userCreateRequest = UserCreateRequest.builder()
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
    public void 평가_생성_테스트() {

        ReviewRequest request = ReviewRequest.builder()
                .restaurantId(1)
                .score(4)
                .content("맛있어요")
                .build();

        HttpEntity<ReviewRequest> entity = new HttpEntity<>(request, httpHeaders);

        ResponseEntity<RestaurantDetail> response = testRestTemplate.exchange("/api/reviews", HttpMethod.POST, entity,
                RestaurantDetail.class);
        RestaurantDetail result = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getReviewResponses().get(0).getUsername()).isEqualTo(userCreateRequest.getUsername());

    }


}
