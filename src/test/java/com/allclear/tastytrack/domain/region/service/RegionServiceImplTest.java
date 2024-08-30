package com.allclear.tastytrack.domain.region.service;

import com.allclear.tastytrack.domain.region.domain.Region;
import com.allclear.tastytrack.domain.region.dto.RegionGroupResponse;
import com.allclear.tastytrack.domain.region.dto.RegionResponse;
import com.allclear.tastytrack.domain.region.repository.RegionRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionServiceImpl regionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    
    @Test
    @DisplayName("지역구 정보를 성공적으로 조회하고 그룹화하여 반환합니다.")
    void testGetRegionInfoSuccess(){
        // given : 준비된 지역 데이턴
        List<Region> regions = Arrays.asList(
                new Region(1, "서울특별시", "강남구", 37.514575, 127.0495556),
                new Region(2, "서울특별시", "강동구", 37.52736667, 127.1258639),
                new Region(3, "경기도", "용인시", 37.2411, 127.1776) // 지역별로 Grouping 되는지 확인
        );

        // when
        when(regionRepository.findAll()).thenReturn(regions);

        // then
        RegionGroupResponse result = regionService.getRegionInfo();
        assertEquals(2, result.getRegions().size()); // 결과가 그룹화되었는지 확인

        RegionResponse seoul = result.getRegions().stream().filter(r -> r.getDosi().equals("서울특별시")).findFirst().orElse(null);
        assertNotNull(seoul);
        assertEquals(2, seoul.getRegionCount()); // 서울특별시에 2개의 구가 있어야 함

        RegionResponse gyeonggi = result.getRegions().stream().filter(r -> r.getDosi().equals("경기도")).findFirst().orElse(null);
        assertNotNull(gyeonggi);
        assertEquals(1, gyeonggi.getRegionCount());

    }

    @Test
    @DisplayName("지역 정보 데이터가 없을 때 예외 테스트를 수행합니다.")
    void testGetRegionInfoNoData(){
        // given 빈 데이터
        when(regionRepository.findAll()).thenReturn(Collections.emptyList());

        // when & then 데이터가 없을 때
        CustomException exception = assertThrows(CustomException.class, ()-> regionService.getRegionInfo());
        assertEquals(ErrorCode.NO_REGION_DATA, exception.getErrorCode());
    }


}
