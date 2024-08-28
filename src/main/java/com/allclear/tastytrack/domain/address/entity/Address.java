package com.allclear.tastytrack.domain.address.entity;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String dosi;

	@Column(nullable = false)
	private String sgg;

	@Column(nullable = false)
	// 경도
	private Double lon;

	@Column(nullable = false)
	// 위도
	private Double lat;

	@Column(nullable = false)
	private String zipCode;
}
