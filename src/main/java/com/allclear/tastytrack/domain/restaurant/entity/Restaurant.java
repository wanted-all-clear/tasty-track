package com.allclear.tastytrack.domain.restaurant.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String code;

	@Column(nullable = false)
	private String type;

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	@ColumnDefault("0.0")
	private double rateScore;

	@Column(nullable = false)
	private String oldAddress;

	@Column(nullable = false)
	private String newAddress;

	@Column(nullable = false)
	private String lon;

	@Column(nullable = false)
	private String lat;

	@JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime lastModts;

}
