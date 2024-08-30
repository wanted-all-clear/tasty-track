package com.allclear.tastytrack.domain.restaurant.coordinate.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CoordinateResponse {
    private List<Documents> documents;
    private CoordinateMeta meta;

}
