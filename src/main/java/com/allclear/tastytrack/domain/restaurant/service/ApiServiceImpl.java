package com.allclear.tastytrack.domain.restaurant.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.allclear.tastytrack.domain.restaurant.dto.LocalDataResponse;
import com.allclear.tastytrack.domain.restaurant.dto.RawRestaurantResponse;
import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RawRestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

    private final RawRestaurantRepository rawRestaurantRepository;
    private final RestaurantRepository restaurantRepository;
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String apiKey; // API 인증키

    /**
     * 1. 서울 맛집 데이터를 수집하여 DB 맛집 원본 테이블에 저장한 후, 전처리 작업을 거쳐 가공 맛집 테이블에 데이터를 저장합니다.
     * 작성자 : 유리빛나
     *
     * @param startIndex 요청 시작 위치
     * @param endIndex   요청 종료 위치
     */
    @Transactional
    public void getRawRestaurants(String startIndex, String endIndex) {

        // 공공데이터 요청을 위한 URL 구성
        String apiUrl = "http://openapi.seoul.go.kr:8088"; // URL
        String responseType = "json";                      // 요청파일 타입
        String serviceName = "LOCALDATA_072404";           // 서비스명

        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment(apiKey, responseType, serviceName, startIndex, endIndex)
                .build()
                .encode()
                .toUri();

        // RestTemplate을 사용하여 GET 요청 전송
        RestTemplate template = new RestTemplate();
        String jsonResponse;

        try {
            jsonResponse = template.getForObject(uri, String.class);
            log.info("JSON 응답:" + jsonResponse);
        } catch (RestClientException e) {
            // HTTP 요청 관련 예외 처리
            log.error("API 요청 실패: {}", uri, e);
            return; // 요청 실패 시 메서드 종료
        }

        // JSON 응답 데이터를 파싱하여 엔티티로 변환
        try {
            LocalDataResponse localDataResponse = objectMapper.readValue(jsonResponse, LocalDataResponse.class);

            List<RawRestaurantResponse> rows = localDataResponse.getLocalData().getRawRestaurantResponses();

            // 각 데이터를 원본 맛집 테이블에 저장
            for (RawRestaurantResponse raw : rows) {
                RawRestaurant restaurant = getRawRestaurantBuilder(raw);

                rawRestaurantRepository.save(restaurant);
            }
        } catch (JsonProcessingException e) {
            // JSON 파싱 관련 예외 처리
            log.error("JSON 파싱 실패: {}", jsonResponse, e);
        }

        // 전처리 후 가공된 데이터를 가공 맛집 테이블에 저장
        preprocessing();

    }

    /**
     * 2. 원본 맛집 데이터 전처리 후 가공된 데이터를 가공 맛집 테이블에 저장합니다.
     * 작성자 : 유리빛나
     */
    public void preprocessing() {

        // 원본 맛집 테이블 모든 데이터 조회
        List<RawRestaurant> rawRestaurantList = rawRestaurantRepository.findAll();

        for (RawRestaurant rawRestaurant : rawRestaurantList) {
            // 폐업일자가 있는 데이터는 저장 제외
            if (!rawRestaurant.getDcbymd().isEmpty()) {
                continue;
            }

            // 원본 데이터를 가공하여 새로운 엔티티 생성
            Restaurant restaurant = getRestaurantBuilder(rawRestaurant);

            // 가공된 데이터 저장
            restaurantRepository.save(restaurant);
        }
    }

    /**
     * 공공데이터의 맛집 응답 데이터를 원본 맛집 Entity에 저장하기 위해 Builder 패턴으로 RawRestaurant 객체를 생성하는 메서드
     * 작성자 : 유리빛나
     *
     * @param raw 공공데이터의 맛집 응답 데이터
     * @return 맛집 응답 데이터가 저장된 원본 맛집 Entity 객체
     */
    private RawRestaurant getRawRestaurantBuilder(RawRestaurantResponse raw) {

        return RawRestaurant.builder()
                .mgtno(raw.getMgtno())
                .bplcnm(raw.getBplcnm())
                .uptaenm(raw.getUptaenm())
                .dtlstategbn(raw.getDtlstategbn())
                .sitewhladdr(raw.getSitewhladdr())
                .rdnwhladdr(raw.getRdnwhladdr())
                .lon(raw.getLon())
                .lat(raw.getLat())
                .lastmodts(raw.getLastmodts())
                .dcbymd(raw.getDcbymd())
                .build();
    }

    /**
     * 원본 맛집 객체의 최종 수정일자를 문자열에서 LocalDateTime 타입으로 파싱하기 위한 메서드
     * 작성자 : 유리빛나
     *
     * @param lastmodts 원본 맛집 객체의 최종 수정일자를 나타내는 문자열
     * @return 형식에 맞게 파싱된 LocalDateTime 객체
     */
    private LocalDateTime parseLastmodts(String lastmodts) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(lastmodts, formatter);
    }

    /**
     * 원본 맛집 데이터를 가공 맛집 Entity에 저장하기 위해 Builder 패턴으로 Restaurant 객체를 생성하는 메서드
     * 작성자 : 유리빛나
     *
     * @param rawRestaurant 원본 맛집 데이터
     * @return 원본 맛집 데이터가 저장된 가공 맛집 Entity 객체
     */
    private Restaurant getRestaurantBuilder(RawRestaurant rawRestaurant) {

        return Restaurant.builder()
                .code(rawRestaurant.getMgtno())
                .name(rawRestaurant.getBplcnm())
                .type(rawRestaurant.getUptaenm())
                .status(rawRestaurant.getDtlstategbn())
                .oldAddress(rawRestaurant.getSitewhladdr())
                .newAddress(rawRestaurant.getRdnwhladdr())
                .lon(Double.valueOf(rawRestaurant.getLon()))
                .lat(Double.valueOf(rawRestaurant.getLat()))
                .lastUpdatedAt(parseLastmodts(rawRestaurant.getLastmodts()))
                .build();

    }

}
