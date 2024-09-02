package com.allclear.tastytrack.domain.restaurant.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.allclear.tastytrack.domain.review.dto.ReviewResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RestaurantDetail {

    private String name;

    private String type;

    private String status;

    private Double rateScore;

    private String oldAddress;

    private String newAddress;

    private Double lon;

    private Double lat;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdateAt;

    @Setter
    private List<ReviewResponse> reviewResponses;

}
