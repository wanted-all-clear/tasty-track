package com.allclear.tastytrack.restaurant;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantDetail;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestaurantControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	@DisplayName("맛집 상세 정보 조회 통합 테스트 입니다.")
	public void 맛집_상세정보_조회_테스트() {

		HttpEntity<Integer> entity = new HttpEntity<>(4);
		String url = "/api/restaurants";

		ResponseEntity<RestaurantDetail> responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, entity,
				RestaurantDetail.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(200);
		assertThat(responseEntity.getBody().getName()).isEqualTo("");

	}

}
