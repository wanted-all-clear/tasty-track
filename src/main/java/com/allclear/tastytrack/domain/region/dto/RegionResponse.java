package com.allclear.tastytrack.domain.region.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegionResponse {
    private String dosi;
    private List<String> sgg;
    private int regionCount; // sgg의 총 개수
}
