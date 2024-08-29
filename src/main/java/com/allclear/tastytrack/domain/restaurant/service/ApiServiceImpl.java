package com.allclear.tastytrack.domain.restaurant.service;

import com.allclear.tastytrack.domain.restaurant.dto.LocalDataResponse;
import com.allclear.tastytrack.domain.restaurant.dto.RawRestaurantResponse;
import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RawRestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
     * 1. 서울 맛집 데이터를 수집하여 DB 맛집 원본 테이블에 저장합니다.
     * 작성자 : 유리빛나
     *
     * @param startIndex 요청 시작 위치
     * @param endIndex   요청 종료 위치
     */
    @Transactional
    public void getRawRestaurants(String startIndex, String endIndex) {

        // 요청 URL 구성
        String apiUrl = "http://openapi.seoul.go.kr:8088"; // URL
        String responseType = "json";                      // 요청파일 타입
        String serviceName = "LOCALDATA_072404";           // 서비스명

        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment(apiKey, responseType, serviceName, startIndex, endIndex)
                .build()
                .encode()
                .toUri();

        // RestTemplate 사용하여 GET 요청 보내기
        RestTemplate template = new RestTemplate();

        String jsonResponse = template.getForObject(uri, String.class);
        log.info("JSON 응답:" + jsonResponse);

        // JSON 데이터를 파싱하여 엔티티로 변환
        // ObjectMapper를 사용하여 역직렬화 (JSON 문자열을 Java 객체로 변환)
        try {
            LocalDataResponse localDataResponse = objectMapper.readValue(jsonResponse, LocalDataResponse.class);

            List<RawRestaurantResponse> rows = localDataResponse.getLocalData().getRawRestaurantResponses();

            for (RawRestaurantResponse raw : rows) {
                RawRestaurant restaurant = getRawRestaurantBuilder(raw);

                rawRestaurantRepository.save(restaurant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 원본 맛집 데이터 전처리 후 가공된 데이터를 가공 맛집 테이블에 저장
        preprocessing();

    }

    /**
     * 2. 원본 맛집 데이터 전처리 후 가공된 데이터를 가공 맛집 테이블에 저장합니다.
     * 작성자 : 유리빛나
     */
    public void preprocessing() {

        List<RawRestaurant> rawRestaurantList = rawRestaurantRepository.findAll();

        for (RawRestaurant rawRestaurant : rawRestaurantList) {

            Restaurant restaurant = Restaurant.builder()
                    .code(rawRestaurant.getMgtno())
                    .name(rawRestaurant.getBplcnm())
                    .type(rawRestaurant.getUptaenm())
                    .status(rawRestaurant.getDtlstategbn())
                    .oldAddress(rawRestaurant.getSitewhladdr())
                    .newAddress(rawRestaurant.getRdnwhladdr())
                    .lon(rawRestaurant.getLon())
                    .lat(rawRestaurant.getLat())
                    .lastUpdatedAt(parseLastmodts(rawRestaurant.getLastmodts()))
                    .build();

            restaurantRepository.save(restaurant);
        }
    }

    /**
     * 공공데이터의 맛집 응답 데이터를 원본 맛집 DB Entity에 저장하기 위해 Builder 패턴으로 객체를 생성하는 메서드
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
     * 원본 맛집 객체의 최종 수정일자를 문자열에서 LocalDateTime 타입으로 파싱합니다.
     * 작성자 : 유리빛나
     *
     * @param lastmodts 원본 맛집 객체의 최종 수정일자를 나타내는 문자열
     * @return 형식에 맞게 파싱된 LocalDateTime 객체
     */
    private LocalDateTime parseLastmodts(String lastmodts) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(lastmodts, formatter);
    }

}
