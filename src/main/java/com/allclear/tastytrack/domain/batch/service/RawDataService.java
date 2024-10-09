package com.allclear.tastytrack.domain.batch.service;

import com.allclear.tastytrack.domain.batch.dto.LocalDataResponse;
import com.allclear.tastytrack.domain.batch.dto.RawRestaurantResponse;
import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RawDataService {

    private final ObjectMapper objectMapper;

    @Value("${API_URL}")
    private String apiUrl; // API URL

    @Value("${API_KEY}")
    private String apiKey; // API 인증키

    @Value("${API_RESPONSE_TYPE}")
    private String responseType; // 요청파일 타입

    @Value("${API_SERVICE_NAME}")
    private String serviceName;  // 서비스명

    private static final int PAGE_SIZE = 100; // 1회 호출 시 응답받을 데이터 수

    /**
     * 공공데이터 API에서 맛집 데이터를 수집하여 원본 테이블에 저장하는 공통 로직을 처리하는 메서드입니다.
     * 이 메서드는 초기 데이터 구축과 업데이트 모두에서 호출됩니다.
     * 작성자 : 유리빛나
     */
    public List<RawRestaurantResponse> fetchAndSaveCommon() {

        int startIndex = 1;
        int endIndex = PAGE_SIZE;
        int totalCount;

        List<RawRestaurantResponse> jsonRows = new ArrayList<>();

        do {
            // JSON 응답 데이터 파싱 및 맛집 원본 DB 저장
            totalCount = parseAndSaveRawRestaurantData(String.valueOf(startIndex), String.valueOf(endIndex), jsonRows);

            // 페이지 업데이트
            startIndex += PAGE_SIZE;
            endIndex += PAGE_SIZE;

            // 마지막 페이지 처리를 위한 조건
            if (endIndex > totalCount) {
                endIndex = totalCount;
            }
        } while (startIndex <= totalCount);

        return jsonRows;
    }

    /**
     * JSON 형식으로 받은 공공데이터 응답을 파싱하여 맛집 원본 테이블에 저장하는 메서드입니다.
     * 기존 데이터는 최종 수정일자가 변경된 경우에만 업데이트하며, 새로운 데이터는 추가합니다.
     * 작성자 : 유리빛나
     *
     * @param startIndex 요청 시작 위치
     * @param endIndex   요청 종료 위치
     * @return 전체 데이터 건수
     */
    private int parseAndSaveRawRestaurantData(String startIndex, String endIndex, List<RawRestaurantResponse> jsonRows) {
        // 공공데이터 요청을 위한 URL 구성
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
        } catch (RestClientException e) {
            // HTTP 요청 관련 예외 처리
            throw new CustomException(ErrorCode.API_NOT_FOUND);
        }

        // JSON 응답 데이터를 파싱하여 엔티티로 변환
        try {
            LocalDataResponse localDataResponse = objectMapper.readValue(jsonResponse, LocalDataResponse.class);
            List<RawRestaurantResponse> rows = localDataResponse.getLocalData().getRawRestaurantResponses();

            // 전체 데이터 건수 <테스트용으로 300건만 조회>
            int totalCount = localDataResponse.getLocalData().getListTotalCount() - 509587;

            // 각 JSON 응답 처리
            for (RawRestaurantResponse raw : rows) {

                // 영업상태가 폐업이 아닌 데이터만 맛집 원본 저장
                if (!raw.getDtlstategbn().equals("02")) {
                    jsonRows.add(getJsonRowsBuilder(raw));
                }
            }
            return totalCount; // 전체 데이터 건수 반환
        } catch (JsonProcessingException e) {
            // JSON 파싱 관련 예외 처리
            throw new CustomException(ErrorCode.JSON_PARSING);
        }
    }

    /**
     * 공공데이터의 맛집 응답 데이터를 맛집 RawRestaurantResponse에 저장하기 위해 Builder 패턴으로 RawRestaurantResponse 객체를 생성하는 메서드
     * 작성자 : 유리빛나
     *
     * @param jsonRows 공공데이터의 맛집 응답 데이터
     * @return 맛집 응답 데이터가 저장된 맛집 RawRestaurantResponse 객체
     */
    private RawRestaurantResponse getJsonRowsBuilder(RawRestaurantResponse jsonRows) {

        return RawRestaurantResponse.builder()
                .mgtno(jsonRows.getMgtno())
                .bplcnm(jsonRows.getBplcnm())
                .uptaenm(jsonRows.getUptaenm())
                .dtlstategbn(jsonRows.getDtlstategbn())
                .sitewhladdr(jsonRows.getSitewhladdr())
                .rdnwhladdr(jsonRows.getRdnwhladdr())
                .lon(jsonRows.getLon())
                .lat(jsonRows.getLat())
                .lastmodts(jsonRows.getLastmodts())
                .build();
    }

    /**
     * 맛집 원본 객체의 최종 수정일자를 문자열에서 LocalDateTime 타입으로 파싱하기 위한 메서드
     * 작성자 : 유리빛나
     *
     * @param lastmodts 맛집 원본 객체의 최종 수정일자를 나타내는 문자열
     * @return 형식에 맞게 파싱된 LocalDateTime 객체
     */
    public LocalDateTime parseLastmodts(String lastmodts) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(lastmodts, formatter);
    }

    /**
     * 공공데이터의 맛집 응답 데이터를 맛집 원본 Entity에 저장하기 위해 Builder 패턴으로 RawRestaurant 객체를 생성하는 메서드
     * 작성자 : 유리빛나
     *
     * @param jsonRows 공공데이터의 맛집 응답 데이터
     * @return 맛집 응답 데이터가 저장된 맛집 원본 Entity 객체
     */
    public RawRestaurant getRawRestaurantBuilder(RawRestaurantResponse jsonRows) {

        return RawRestaurant.builder()
                .mgtno(jsonRows.getMgtno())
                .bplcnm(jsonRows.getBplcnm())
                .uptaenm(jsonRows.getUptaenm())
                .dtlstategbn(jsonRows.getDtlstategbn())
                .sitewhladdr(jsonRows.getSitewhladdr())
                .rdnwhladdr(jsonRows.getRdnwhladdr())
                .lon(jsonRows.getLon())
                .lat(jsonRows.getLat())
                .lastmodts(jsonRows.getLastmodts())
                .build();
    }

    /**
     * 맛집 원본 데이터를 맛집 가공 Entity에 저장하기 위해 Builder 패턴으로 Restaurant 객체를 생성하는 메서드
     * 작성자 : 유리빛나
     *
     * @param rawRestaurant 맛집 원본 데이터
     * @return 맛집 원본 데이터가 저장된 맛집 가공 Entity 객체
     */
    public Restaurant getRestaurantBuilder(RawRestaurant rawRestaurant) {

        Double lon = rawRestaurant.getLon() != null ? Double.valueOf(rawRestaurant.getLon()) : null;
        Double lat = rawRestaurant.getLat() != null ? Double.valueOf(rawRestaurant.getLat()) : null;

        return Restaurant.builder()
                .code(rawRestaurant.getMgtno())
                .name(rawRestaurant.getBplcnm())
                .type(rawRestaurant.getUptaenm())
                .status(rawRestaurant.getDtlstategbn())
                .oldAddress(rawRestaurant.getSitewhladdr())
                .newAddress(rawRestaurant.getRdnwhladdr())
                .lon(lon)
                .lat(lat)
                .lastUpdatedAt(parseLastmodts(rawRestaurant.getLastmodts()))
                .build();
    }

}
