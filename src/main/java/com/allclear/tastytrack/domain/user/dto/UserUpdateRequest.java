package com.allclear.tastytrack.domain.user.dto;

import com.allclear.tastytrack.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {

    @NotNull(message = "경도 값은 필수 입력값입니다.")
    private Double lon;
    @NotNull(message = "위도 값은 필수 입력값입니다.")
    private Double lat;
    private boolean lunchRecommendYn;

    public User toUpdateEntity(User user) {

        return User.builder()
                .username(user.getUsername())
                .lon(this.getLon())
                .lat(this.getLat())
                .lunchRecommendYn(this.lunchRecommendYn)
                .build();
    }

}
