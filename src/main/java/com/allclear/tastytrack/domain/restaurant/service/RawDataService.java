package com.allclear.tastytrack.domain.restaurant.service;

import com.allclear.tastytrack.domain.restaurant.dto.LocalDataResponse;
import com.allclear.tastytrack.domain.restaurant.dto.RawRestaurantResponse;
import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RawRestaurantRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawDataService {

    private final RawRestaurantRepository rawRestaurantRepository;
    private final ObjectMapper objectMapper;

    private final DataProcessingService dataProcessingService;

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
     * 1. 초기화 메서드 (의존성 주입 완료 후, 초기화 작업을 수행할 메서드 지정)
     * 작성자 : 유리빛나
     */
    @PostConstruct
    public void init() throws Exception {

        fetchAndSaveInitDatas(); // 초기화 시 전체 데이터 페이징 처리

    }

    /**
     * 2. 서울 맛집 데이터를 최초 수집하여 원본 테이블에 저장하고, 가공된 데이터를 가공 테이블에 저장하여 초기 데이터를 구축합니다.
     * 작성자 : 유리빛나
     */
    public void fetchAndSaveInitDatas() throws Exception {

        // 최초 맛집 데이터 수집 및 맛집 원본 DB 저장
        fetchAndSaveCommon();

        // 최초 맛집 가공 DB 저장
        dataProcessingService.preprocessingAndSaveInitRestaurant();
    }

    /**
     * 3. 매일 자정에 서울 맛집 데이터 수집 및 최종수정일자가 변경된 맛집 원본을 조회하여 맛집 가공 DB의 데이터를 업데이트합니다.
     * 작성자 : 유리빛나
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(fixedRate = 30_000) // 30초마다 실행되는 테스트용 스케줄러
    public void fetchAndSaveUpdatedDatas() throws Exception {

        // 맛집 데이터 수집 및 업데이트된 맛집 원본 DB만 저장
        fetchAndSaveCommon();

        // 업데이트된 데이터만 맛집 가공 DB 저장
        dataProcessingService.preprocessingAndSaveUpdatedAtNotMatchingRestaurant();
    }

    /**
     * 공공데이터 API에서 맛집 데이터를 수집하여 원본 테이블에 저장하는 공통 로직을 처리하는 메서드입니다.
     * 이 메서드는 초기 데이터 구축과 업데이트 모두에서 호출됩니다.
     * 작성자 : 유리빛나
     */
    void fetchAndSaveCommon() {

        int startIndex = 1;
        int endIndex = PAGE_SIZE;
        int totalCount;

        do {
            // JSON 응답 데이터 파싱 및 맛집 원본 DB 저장
            totalCount = parseAndSaveRawRestaurantData(String.valueOf(startIndex), String.valueOf(endIndex));

            // 페이지 업데이트
            startIndex += PAGE_SIZE;
            endIndex += PAGE_SIZE;

            // 마지막 페이지 처리를 위한 조건
            if (endIndex > totalCount) {
                endIndex = totalCount;
            }
        } while (startIndex <= totalCount);
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
    private int parseAndSaveRawRestaurantData(String startIndex, String endIndex) {
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
                String mgtno = raw.getMgtno();
                RawRestaurant existingRestaurant = rawRestaurantRepository.findByMgtno(mgtno);

                if (existingRestaurant != null) {
                    // 이미 존재하는 경우 업데이트
                    LocalDateTime existingLastmodts = parseLastmodts(existingRestaurant.getLastmodts());
                    LocalDateTime newLastmodts = parseLastmodts(raw.getLastmodts());

                    if (!existingLastmodts.equals(newLastmodts)) {
                        // 최종수정일자가 다른 경우에만 업데이트
                        existingRestaurant.updateWithRawRestaurant(raw);
                        rawRestaurantRepository.save(existingRestaurant);
                    }
                } else {
                    // 신규 데이터인 경우에만 저장
                    RawRestaurant newRestaurant = getRawRestaurantBuilder(raw);

                    rawRestaurantRepository.save(newRestaurant);
                }
            }
            return totalCount; // 전체 데이터 건수 반환
        } catch (JsonProcessingException e) {
            // JSON 파싱 관련 예외 처리
            throw new CustomException(ErrorCode.JSON_PARSING);
        }
    }

    /**
     * 공공데이터의 맛집 응답 데이터를 맛집 원본 Entity에 저장하기 위해 Builder 패턴으로 RawRestaurant 객체를 생성하는 메서드
     * 작성자 : 유리빛나
     *
     * @param raw 공공데이터의 맛집 응답 데이터
     * @return 맛집 응답 데이터가 저장된 맛집 원본 Entity 객체
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
                .build();
    }

    /**
     * 맛집 원본 객체의 최종 수정일자를 문자열에서 LocalDateTime 타입으로 파싱하기 위한 메서드
     * 작성자 : 유리빛나
     *
     * @param lastmodts 맛집 원본 객체의 최종 수정일자를 나타내는 문자열
     * @return 형식에 맞게 파싱된 LocalDateTime 객체
     */
    private LocalDateTime parseLastmodts(String lastmodts) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(lastmodts, formatter);
    }

}
