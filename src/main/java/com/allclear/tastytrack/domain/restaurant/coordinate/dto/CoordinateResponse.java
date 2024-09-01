package com.allclear.tastytrack.domain.restaurant.coordinate.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CoordinateResponse { // 최상위 응답객체
    private List<Documents> documents;
    private CoordinateMeta meta;

}
