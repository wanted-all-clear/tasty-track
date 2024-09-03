package com.allclear.tastytrack.domain.batch.config;

import com.allclear.tastytrack.domain.batch.dto.RawRestaurantResponse;
import com.allclear.tastytrack.domain.batch.listener.JobCompletionNotificationListener;
import com.allclear.tastytrack.domain.batch.service.RawDataService;
import com.allclear.tastytrack.domain.restaurant.coordinate.dto.Coordinate;
import com.allclear.tastytrack.domain.restaurant.coordinate.service.CoordinateService;
import com.allclear.tastytrack.domain.restaurant.entity.RawRestaurant;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.allclear.tastytrack.domain.restaurant.repository.RawRestaurantRepository;
import com.allclear.tastytrack.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing // 스프링 배치를 작동시켜준다.
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final RawRestaurantRepository rawRestaurantRepository;
    private final RestaurantRepository restaurantRepository;

    private final RawDataService rawDataService;
    private final CoordinateService coordinateService;

    private final JobCompletionNotificationListener jobCompletionNotificationListener;

    // 1. Job 으로 하나의 배치 작업을 정의
    @Bean
    public Job fetchJob() {

        // fetchJob : 해당 Job을 지칭할 이름
        return new JobBuilder("fetchJob", jobRepository)
                .start(fetchAndSaveStep()) // 이 Job 작업에서 처음 시작할 Step
                .next(processDataStep())   // 다음 Step
                .listener(jobCompletionNotificationListener) // Job 소요시간 측정
                .build();
    }

    // 2. Step 에서 실제 배치 처리 수행, 읽기 > 처리 > 쓰기 과정 구상
    @Bean
    public Step fetchAndSaveStep() { // 원본 맛집 Step

        // chunck : 대량의 데이터를 끊어서 처리할 최소 단위 (1회 호출 시 응답받을 데이터 수)
        // platformTransactionManager : 청크가 진행되다가 실패했을 때, 다시 처리할 수 있도록 롤백을 진행한다든지, 다시 처리하도록 세팅해줌
        return new StepBuilder("fetchAndSaveStep", jobRepository)
                .<RawRestaurantResponse, RawRestaurant>chunk(1000, platformTransactionManager)
                .reader(SeoulRestaurantReader())       // 읽는 메서드 자리
                .processor(SeoulRestaurantProcessor()) // 처리 메서드 자리
                .writer(seoulRestaurantWriter())       // 쓰기 메서드 자리
                .build();
    }

    @Bean
    public Step processDataStep() {  // 가공 맛집 Step

        return new StepBuilder("processDataStep", jobRepository)
                .<RawRestaurant, Restaurant>chunk(1000, platformTransactionManager)
                .reader(restaurantReader())
                .processor(restaurantProcessor())
                .writer(restaurantWriter())
                .build();
    }

    // 3. Reader : 읽어오는 작업 수행
    @Bean
    public ItemReader<RawRestaurantResponse> SeoulRestaurantReader() { // 원본 맛집 Reader

        // RawDataService 에서 공공데이터 Open API를 호출하여 받은 응답 값
        List<RawRestaurantResponse> jsonRows = rawDataService.fetchAndSaveCommon();
        return new ListItemReader<>(jsonRows);
    }

    @Bean
    public RepositoryItemReader<RawRestaurant> restaurantReader() {    // 가공 맛집 Reader

        return new RepositoryItemReaderBuilder<RawRestaurant>()
                .name("restaurantReader")
                .pageSize(1000)
                .methodName("findAll") // Repository의 findAll 메서드
                .repository(rawRestaurantRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    // 4. Processor : 읽어오는 Data 중간 처리
    @Bean
    public ItemProcessor<RawRestaurantResponse, RawRestaurant> SeoulRestaurantProcessor() { // 원본 맛집 Processor

        // <[Reader에서 읽어들일 데이터 타입], [Writer에서 쓸 데이터 타입]>
        return new ItemProcessor<RawRestaurantResponse, RawRestaurant>() {

            // process 메서드를 통해 item(RawRestaurantResponse)에 읽어들인 데이터가 담기게 됨
            @Override
            public RawRestaurant process(RawRestaurantResponse item) throws Exception {

                // 기존 데이터가 있다면 최종 수정일자 비교 후 업데이트
                RawRestaurant existingRawRestaurant = rawRestaurantRepository.findByMgtno(item.getMgtno());

                // 이미 존재하는 경우
                if (existingRawRestaurant != null) {
                    LocalDateTime existingLastmodts = rawDataService.parseLastmodts(existingRawRestaurant.getLastmodts());
                    LocalDateTime newLastmodts = rawDataService.parseLastmodts(item.getLastmodts());

                    // 최종수정일자가 다른 경우에만 업데이트
                    if (!existingLastmodts.equals(newLastmodts)) {
                        existingRawRestaurant.updateWithRawRestaurant(item);
                        log.info("업데이트된 원본 맛집 : {}, {}", existingRawRestaurant.getBplcnm(), existingRawRestaurant.getMgtno());
                        return existingRawRestaurant; // 업데이트할 원본 맛집 반환
                    } else {
                        return null;                  // 수정일자가 같으면 업데이트 제외
                    }
                } else {
                    RawRestaurant rawRestaurant = rawDataService.getRawRestaurantBuilder(item);
                    log.info("저장된 원본 맛집 : {}, {}", rawRestaurant.getBplcnm(), rawRestaurant.getMgtno());

                    return rawRestaurant; // 저장할 신규 데이터 반환

                }
            }
        };
    }

    @Bean
    public ItemProcessor<RawRestaurant, Restaurant> restaurantProcessor() { // 가공 맛집 Processor

        return new ItemProcessor<RawRestaurant, Restaurant>() {
            @Override
            public Restaurant process(RawRestaurant item) throws Exception {

                int counter = 0; // 로깅에 사용될 카운터

                // 맛집 원본 데이터의 도로명 주소로 주소 검색 openAPI를 활용해 WGS84 좌표계 타입의 위도, 경도 데이터를 맛집 가공 DB에 저장
                // 도로명 주소가 없을 경우 -> 지번 주소로 위도 경도 데이터 설정
                String addressToUse = item.getRdnwhladdr().isEmpty() ? item.getSitewhladdr() :
                        item.getRdnwhladdr();

                log.info("{}번째 저장될 가공 맛집 : {}", item.getId(), item.getBplcnm());

                Coordinate coordinateByRdnwhladder = coordinateService.getCoordinate(addressToUse);

                if (coordinateByRdnwhladder.getLat() == null && coordinateByRdnwhladder.getLon() == null) {
                    Coordinate coordinateBySitewhladdr = coordinateService.getCoordinate(item.getSitewhladdr());
                    log.info("{} 맛집의 지번주소 : {}", item.getBplcnm(), item.getSitewhladdr());
                    log.info("{} 맛집의 지번주소 버전 위도 : {}, 경도 : {}", item.getBplcnm(), coordinateBySitewhladdr.getLon(), coordinateBySitewhladdr.getLat());

                    item.setLon(coordinateBySitewhladdr.getLon());
                    item.setLat(coordinateBySitewhladdr.getLat());

                    // 원본 맛집에 도로명주소, 위도, 경도가 없고, 지번주소도 잘못된 경우 가공 저장 제외
                    if (item.getLat() == null) {
                        log.info("{} 맛집은 도로명주소, 위도, 경도가 존재하지 않고, 지번주소가 조회되지 않아 저장에서 제외되었습니다. ", item.getBplcnm());
                        return null;
                    }
                } else {
                    item.setLon(coordinateByRdnwhladder.getLon());
                    item.setLat(coordinateByRdnwhladder.getLat());
                }

                log.info("{} 맛집의 도로명주소 버전 위도 : {}, 경도 : {}", item.getBplcnm(), coordinateByRdnwhladder.getLon(), coordinateByRdnwhladder.getLat());

                // 가공 테이블에서 동일한 mgtno(code) 값을 가진 데이터를 조회
                Restaurant existingRestaurant = restaurantRepository.findByCode(item.getMgtno());

                if (existingRestaurant != null) {
                    // 기존 데이터가 있는 경우, 최종 수정일자를 비교하여 업데이트
                    LocalDateTime existingLastUpdatedAt = existingRestaurant.getLastUpdatedAt();
                    LocalDateTime newLastUpdatedAt = rawDataService.parseLastmodts(item.getLastmodts());

                    // 최종수정일자가 다른 경우에만 업데이트
                    if (!existingLastUpdatedAt.equals(newLastUpdatedAt)) {
                        // 맛집 원본의 상세영업상태코드가 02인(폐업) 데이터는 맛집 가공의 삭제여부를 true로 저장
                        if ("02".equals(item.getDtlstategbn())) {
                            existingRestaurant.setDeletedYn(1);
                        }

                        existingRestaurant.updateWithNewData(rawDataService.getRestaurantBuilder(item));

                        log.info("업데이트된 가공 맛집 : {}", existingRestaurant.getName());
                        return existingRestaurant; // 업데이트할 가공 맛집 반환
                    }
                } else {
                    // 신규 데이터인 경우에만 저장
                    Restaurant restaurant = rawDataService.getRestaurantBuilder(item);

                    try {
                        return restaurant; // 가공된 데이터 반환
                    } catch (DataIntegrityViolationException e) {
                        log.error("관리번호 {} 가공 맛집 저장 실패 : ", item.getMgtno(), e);
                    }
                }

                return null; // 원본 맛집 데이터가 없을 경우
            }
        };
    }

    // 5. Writer : 처리한 결과 저장
    @Bean
    public ItemWriter<RawRestaurant> seoulRestaurantWriter() { // 원본 맛집 저장 Writer

        return new RepositoryItemWriterBuilder<RawRestaurant>()
                .repository(rawRestaurantRepository) // rawRestaurantRepository 를 통해서
                .methodName("save")                  // save 쿼리 실행
                .build();
    }

    @Bean
    public ItemWriter<Restaurant> restaurantWriter() {         // 가공 맛집 저장 Writer

        return new RepositoryItemWriterBuilder<Restaurant>()
                .repository(restaurantRepository)
                .methodName("save")
                .build();
    }

}
