package com.allclear.tastytrack.domain.region.controller;

import com.allclear.tastytrack.domain.region.dto.RegionGroupResponse;
import com.allclear.tastytrack.domain.region.dto.RegionResponse;
import com.allclear.tastytrack.domain.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
@Tag(name = "Region", description = "지역 관련 API")
public class RegionController {

    private final RegionService regionService;

    @Operation(summary = "지역 정보 조회", description = "서울특별시 지역구별 검색을 위한 지역 정보 조회 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리스트를 성공적으로 조회하였습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegionResponse.class))),
            @ApiResponse(responseCode = "404", description = "지역 정보를 찾을 수 없습니다.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content)
    })
    @GetMapping("")
    public ResponseEntity<List<RegionResponse>> getRegionList() {

        List<RegionResponse> responses = regionService.getRegionList();

        return ResponseEntity.ok(responses);
    }

}
