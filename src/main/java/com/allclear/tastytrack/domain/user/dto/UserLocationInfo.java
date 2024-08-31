package com.allclear.tastytrack.domain.user.dto;

import com.allclear.tastytrack.domain.user.enums.Coordinate;

import jakarta.persistence.Column;

public class UserLocationInfo {

    @Column(nullable = false)
    private double lon;
    @Column(nullable = false)
    private double lat;
    @Column(nullable = false)
    private int distance;

    public double getNothLat() {

        return this.lat + (Coordinate.LAT.getValue() * this.distance);
    }

    public double getSouthLat() {

        return this.lat - (Coordinate.LAT.getValue() * this.distance);
    }

    public double getEastLon() {

        return this.lon + (Coordinate.LON.getValue() * this.distance);
    }

    public double getWestLon() {

        return this.lon - (Coordinate.LON.getValue() * this.distance);
    }

}
