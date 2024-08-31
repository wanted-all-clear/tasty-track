package com.allclear.tastytrack.domain.user.enums;

import lombok.Getter;

@Getter
public enum Coordinate {
    LAT(0.009),
    LON(0.0113);

    private final double value;

    Coordinate(double value) {

        this.value = value;
    }
}
