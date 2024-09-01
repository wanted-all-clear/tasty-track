package com.allclear.tastytrack.domain.user.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserLocationInfo {

    @Column(nullable = false)
    private double lon;
    @Column(nullable = false)
    private double lat;
    @Column(nullable = false)
    private int distance;

}
