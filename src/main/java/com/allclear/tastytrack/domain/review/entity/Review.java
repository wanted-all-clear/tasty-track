package com.allclear.tastytrack.domain.review.entity;

import com.allclear.tastytrack.global.entity.Timestamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private int restaurantId;

    @Column(nullable = false)
    private int score;

    private String content;

    //	@ManyToOne
    //	@JoinColumn(name = "user_id", nullable = false)
    //	private User user;

    //	@ManyToOne(fetch = FetchType.LAZY)  // 다:1 관계 설정
    //	@JoinColumn(name = "restaurantId", nullable = false)  // 외래 키 컬럼 설정
    //	private Restaurant restaurant;  // Restaurant 참조 추가


}
