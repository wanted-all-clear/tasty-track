package com.allclear.tastytrack.domain.restaurant.coordinate.service;

import com.allclear.tastytrack.domain.restaurant.coordinate.dto.Coordinate;

public interface CoordinateService {
    Coordinate getCoordinate(String address) throws Exception;

}
