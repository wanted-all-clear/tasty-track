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
    private int id;                      // 맛집 가공 id

    @Column(unique = true, nullable = false)
    private String code;                 // 관리번호

    @Column(nullable = false, length = 50)
    private String name;                 // 사업장명

    @Column(nullable = false, length = 16)
    private String type;                 // 업태구분명

    @Column(nullable = false, length = 16)
    private String status;               // 상세영업상태코드

    @Column(nullable = false)
    private String oldAddress;           // 지번주소

    @Column(nullable = false)
    private String newAddress;           // 도로명주소

    @Column(nullable = false)
    private Double lon;                  // 경도

    @Column(nullable = false)
    private Double lat;                  // 위도

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdatedAt; // 최종 수정일자

    @Column(nullable = false)
    @ColumnDefault("0.0")
    private double rateScore;            // 평점

    @Column(nullable = false)
    private boolean deletedYn;           // 삭제여부

}
