package com.allclear.tastytrack.domain.restaurant.coordinate.service;


import com.allclear.tastytrack.domain.restaurant.coordinate.dto.Coordinate;
import com.allclear.tastytrack.domain.restaurant.coordinate.dto.CoordinateResponse;
import com.allclear.tastytrack.domain.restaurant.coordinate.dto.Documents;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoordinateServiceImpl implements CoordinateService {

    @Value("${COORDINATE_API_URL}")
    private String coordinateApiUrl; // 주소 검색 openAPI URL
    @Value("${COORDINATE_API_KEY}")
    private String coordinateApiKey; // 주소 검색 openAPI 인증키

    public final ObjectMapper objectMapper;

    /**
     * 도로명 주소또는 지번주소를 받아와 위도, 경도 반환
     * 작성자 : 오예령
     *
     * @param address 도로명 주소 또는 지번주소
     * @return 해당 위치의 위도 경도를 WGS84 좌표계 형태로 반환
     * @throws Exception openAPI 요청 URL 암호화 및 생성,
     */
    public Coordinate getCoordinate(String address) throws Exception {

        // 공공데이터 요청을 위한 URL 구성
        String urlString = coordinateApiUrl + URLEncoder.encode(address, UTF_8);
        URL url = new URL(urlString);

        // RestTemplate을 사용하여 요청 전송
        RestTemplate template = new RestTemplate();
        String jsonResponse;

        // hedaer에 인증키 담아서 전송
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", coordinateApiKey);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        String lon = null, lat = null;

        try {
            jsonResponse = template.exchange(url.toURI(), GET, httpEntity, String.class).getBody();
        } catch (RestClientException e) {
            // HTTP 요청 관련 예외 처리
            log.error("API 요청 실패: {}", url, e);
            return null; // 요청 실패 시 메서드 종료
        }

        // JSON 응답 데이터를 파싱하여 엔티티로 변환
        try {
            CoordinateResponse response = objectMapper.readValue(jsonResponse, CoordinateResponse.class);

            List<Documents> documents = response.getDocuments();

            for (Documents documents1 : documents) {
                lon = documents1.getX();
                lat = documents1.getY();
            }

        } catch (JsonProcessingException e) {
            // JSON 파싱 관련 예외 처리
            log.error("JSON 파싱 실패: {}", jsonResponse, e);
        }

        // 해당 주소의 위도, 경도 값 반환
        return new Coordinate(lon, lat);
    }

}
