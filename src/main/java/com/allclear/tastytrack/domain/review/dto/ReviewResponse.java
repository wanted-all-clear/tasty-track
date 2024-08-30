package com.allclear.tastytrack.domain.review.dto;

import com.allclear.tastytrack.global.entity.Timestamped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponse extends Timestamped {

	private String username;
	private int score;
	private String content;

}
