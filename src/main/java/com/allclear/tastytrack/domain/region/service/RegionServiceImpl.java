package com.allclear.tastytrack.domain.region.service;

import com.allclear.tastytrack.domain.region.entity.Region;
import com.allclear.tastytrack.domain.region.dto.RegionGroupResponse;
import com.allclear.tastytrack.domain.region.dto.RegionResponse;
import com.allclear.tastytrack.domain.region.repository.RegionRepository;
import com.allclear.tastytrack.global.exception.CustomException;
import com.allclear.tastytrack.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    /**
     * 서울특별시 행정구역을 불러옵니다.
     * 작성자: 배서진
     *
     * @return RegionGroupResponse 객체
     * 향후 지역 확장성을 고려하여, List 안에 ResponseDTO를 담는 형식으로 하였습니다.
     */
    @Override
    public List<RegionResponse> getRegionList() {

        try {
            List<Region> regions = regionRepository.findAll();

            if (regions.isEmpty()) {
                throw new CustomException(ErrorCode.NO_REGION_DATA);
            }

            // 동일한 dosi(시,도) 값을 가진 sgg를 Map으로 그룹화하고,
            // regionResponseDTO 형태에 맞게 변환하여 제공합니다.
            Map<String, List<String>> groupedByDosi = regions.stream()
                    .collect(Collectors.groupingBy(
                            Region::getDosi,
                            Collectors.mapping(Region::getSgg, Collectors.toList())
                    ));

            List<RegionResponse> regionResponses = groupedByDosi.entrySet().stream()
                    .map(entry -> RegionResponse.builder()
                            .dosi(entry.getKey())
                            .sgg(entry.getValue())
                            .regionCount(entry.getValue().size())
                            .build())
                    .collect(Collectors.toList());

            return regionResponses;

        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATABASE_EXCEPTION);
        }
    }

}
