package com.allclear.tastytrack.domain.review.entity;

import java.time.LocalDateTime;

import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
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

	@JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDateTime createdAt;

//	@ManyToOne
//	@JoinColumn(name = "user_id", nullable = false)
//	private User user;

//	@ManyToOne(fetch = FetchType.LAZY)  // 다:1 관계 설정
//	@JoinColumn(name = "restaurantId", nullable = false)  // 외래 키 컬럼 설정
//	private Restaurant restaurant;  // Restaurant 참조 추가


}
