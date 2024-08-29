package com.allclear.tastytrack.restaurant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestaurantControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	@DisplayName("맛집 상세 정보 조회 통합 테스트 입니다.")
	public void 맛집_상세정보_조회_테스트() {

	}

}
