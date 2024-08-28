package com.allclear.tastytrack.domain.user.dto;

import com.allclear.tastytrack.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private String username;
    private Double lon;
    private Double lat;
    private boolean lunchRecommendYn;

    public UserInfo(User user) {

        this.username = user.getUsername();
        this.lon = user.getLon();
        this.lat = user.getLat();
        this.lunchRecommendYn = user.isLunchRecommendYn();
    }

}
