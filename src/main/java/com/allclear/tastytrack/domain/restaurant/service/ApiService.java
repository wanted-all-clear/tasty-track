package com.allclear.tastytrack.domain.restaurant.service;

import com.allclear.tastytrack.domain.restaurant.coordinate.dto.Coordinate;
import com.allclear.tastytrack.domain.restaurant.coordinate.service.CoordinateService;
import com.allclear.tastytrack.domain.restaurant.dto.LocalDataResponse;
import com.allclear.tastytrack.domain.restaurant.dto.RawRestaurantResponse;
import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RawRestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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
public class ApiService {

    private final RawRestaurantRepository rawRestaurantRepository;
    private final RestaurantRepository restaurantRepository;
    private final ObjectMapper objectMapper;

    private final CoordinateService coordinateService;

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
     * 2. 서울 맛집 데이터 수집 및 맛집 원본, 맛집 가공 DB에 데이터를 저장하여 초기 데이터를 구축합니다.
     * 작성자 : 유리빛나
     */
    public void fetchAndSaveInitDatas() throws Exception {

        // 최초 맛집 데이터 수집 및 맛집 원본 DB 저장
        fetchAndSaveCommon();

        // 최초 맛집 가공 DB 저장
        preprocessingAndSaveInitRestaurant();
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
        preprocessingAndSaveUpdatedAtNotMatchingRestaurant();
    }

    /**
     * 맛집 데이터를 수집하여 맛집 원본 DB에 데이터를 저장하는 메서드
     * 작성자 : 유리빛나
     */
    private void fetchAndSaveCommon() {

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
     * JSON 응답 데이터를 파싱하여 맛집 원본 DB에 저장하는 메서드
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
            log.error("API 요청 실패: {}", uri, e);
            return 0;
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
            log.error("JSON 파싱 실패: {}", jsonResponse, e);
        }
        return 0;
    }

    /**
     * 맛집 원본 데이터 전처리 후 가공된 데이터를 맛집 가공 테이블에 저장하여 초기 데이터를 구축합니다.
     * 작성자 : 유리빛나
     */
    public void preprocessingAndSaveInitRestaurant() throws Exception {

        // 맛집 원본 리스트의 모든 데이터 조회
        List<RawRestaurant> rawRestaurantList = rawRestaurantRepository.findAll();

        // 맛집 원본 리스트를 맛집 가공 DB에 저장
        saveRestaurantsFromRawRestaurants(rawRestaurantList);
    }

    /**
     * 맛집 원본과 맛집 가공의 최종수정일자가 다른 맛집 원본 데이터 전처리 후 가공된 데이터를 맛집 가공 테이블에 저장합니다.
     * 작성자 : 유리빛나
     */
    public void preprocessingAndSaveUpdatedAtNotMatchingRestaurant() throws Exception {

        // 맛집 원본과 맛집 가공의 최종수정일자가 다른 맛집 원본 테이블의 데이터 조회
        List<RawRestaurant> rawRestaurantList = rawRestaurantRepository.findByLastUpdatedAtNotMatchingRawRestaurants();

        // 업데이트된 맛집 원본 데이터가 없을 경우 리턴
        if (rawRestaurantList.isEmpty()) return;

        // 맛집 원본 리스트를 맛집 가공 DB에 저장
        saveRestaurantsFromRawRestaurants(rawRestaurantList);
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
                .dcbymd(raw.getDcbymd())
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

    /**
     * 맛집 원본 데이터를 맛집 가공 Entity에 저장하기 위해 Builder 패턴으로 Restaurant 객체를 생성하는 메서드
     * 작성자 : 유리빛나
     *
     * @param rawRestaurant 맛집 원본 데이터
     * @return 맛집 원본 데이터가 저장된 맛집 가공 Entity 객체
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

    /**
     * 맛집 원본 리스트를 맛집 가공 DB에 저장합니다.
     * 작성자 : 유리빛나
     *
     * @param rawRestaurantList 맛집 가공 DB에 저장할 맛집 원본 리스트
     */
    private void saveRestaurantsFromRawRestaurants(List<RawRestaurant> rawRestaurantList) throws Exception {

        for (RawRestaurant rawRestaurant : rawRestaurantList) {
            // 도로명주소가 없는 데이터는 일단 제외
            if (rawRestaurant.getRdnwhladdr().isEmpty()) continue;

            // 위도, 경도 값이 누락된 데이터 주입
            if (rawRestaurant.getLon().isEmpty()) {
                Coordinate coordinate = coordinateService.getCoordinate(rawRestaurant.getRdnwhladdr());
                rawRestaurant.setLon(coordinate.getLon());
                rawRestaurant.setLat(coordinate.getLat());
            }

            // 가공 테이블에서 동일한 mgtno(code) 값을 가진 데이터를 조회
            Restaurant existingRestaurant = restaurantRepository.findByCode(rawRestaurant.getMgtno());

            if (existingRestaurant != null) {
                // 기존 데이터가 있는 경우, 최종 수정일자를 비교하여 업데이트
                LocalDateTime existingLastUpdatedAt = existingRestaurant.getLastUpdatedAt();
                LocalDateTime newLastUpdatedAt = parseLastmodts(rawRestaurant.getLastmodts());

                if (!existingLastUpdatedAt.equals(newLastUpdatedAt)) {
                    // 최종수정일자가 다른 경우에만 업데이트
                    existingRestaurant.updateWithNewData(getRestaurantBuilder(rawRestaurant));
                    restaurantRepository.save(existingRestaurant);
                }
            } else {
                // 신규 데이터인 경우에만 저장
                Restaurant restaurant = getRestaurantBuilder(rawRestaurant);

                // 맛집 원본에 폐업일자가 있는 데이터는 맛집 가공의 삭제여부를 true로 저장
                if (!rawRestaurant.getDcbymd().isEmpty()) {
                    restaurant.setDeletedYn(true);
                }

                try {
                    // 가공된 데이터 저장
                    restaurantRepository.save(restaurant);
                } catch (DataIntegrityViolationException e) {
                    log.error("중복된 데이터로 인해 저장 실패: {}", rawRestaurant.getMgtno(), e);
                    // 중복된 데이터로 인한 예외가 발생한 경우 로깅 및 처리
                }
            }
        }
    }

}
