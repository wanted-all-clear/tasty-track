package com.allclear.tastytrack.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Double lon;

    @Column(nullable = false)
    private Double lat;

    @Column
    private boolean lunchRecommendYn;

    public void update(User initUser) {

        this.username = initUser.getUsername();
        this.lon = initUser.getLon();
        this.lat = initUser.getLat();
        this.lunchRecommendYn = initUser.lunchRecommendYn;
    }


}
