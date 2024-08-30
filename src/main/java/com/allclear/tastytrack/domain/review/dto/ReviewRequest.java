package com.allclear.tastytrack.domain.review.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewRequest {

    @Column(unique = true, nullable = false)
    private int userid;

    @Column(unique = true, nullable = false)
    private int restaurantId;

    @Column(unique = true, nullable = false)
    private int score;
    
    private String content;

}
