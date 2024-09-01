package com.allclear.tastytrack.domain.user.enums;

import org.hibernate.annotations.Comment;

import lombok.Getter;

@Getter
public enum Coordinate {

    @Comment("1km를 위도 값으로 변환")
    LAT(0.009),
    
    @Comment("1km를 경도 값으로 변환")
    LON(0.0113);

    private final double value;

    Coordinate(double value) {

        this.value = value;
    }
}
