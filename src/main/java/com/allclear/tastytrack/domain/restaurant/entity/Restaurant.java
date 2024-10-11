package com.allclear.tastytrack.domain.restaurant.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import com.allclear.tastytrack.domain.review.entity.Review;
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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("맛집 가공 id")
    private int id;

    @Column(unique = true, nullable = false)
    @Comment("관리번호")
    private String code;

    @Column(nullable = false, length = 50)
    @Comment("사업장명")
    private String name;

    @Column(nullable = false, length = 16)
    @Comment("업태구분명")
    private String type;

    @Column(nullable = false, length = 16)
    @Comment("상세영업상태코드")
    private String status;

    @Column(name = "old_address", nullable = false)
    @Comment("지번주소")
    private String oldAddress;

    @Column(name = "new_address", nullable = false)
    @Comment("도로명주소")
    private String newAddress;

    @Column(nullable = false)
    @Comment("경도")
    private Double lon;

    @Column(nullable = false)
    @Comment("위도")
    private Double lat;

    @Column(name = "last_updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Comment("최종 수정일자")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "rate_score", nullable = false)
    @ColumnDefault("0.0")
    @Comment("평점")
    private double rateScore;

    @Column(name = "deleted_yn", nullable = false)
    @Comment("삭제여부")
    private int deletedYn;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    private List<Review> reviews;

    public void updateByReview(double rateScore, LocalDateTime updatedAt) {

        this.rateScore = rateScore;
        this.lastUpdatedAt = updatedAt;

    }

    public void updateWithNewData(Restaurant newRestaurant) {

        this.name = newRestaurant.getName();
        this.type = newRestaurant.getType();
        this.status = newRestaurant.getStatus();
        this.oldAddress = newRestaurant.getOldAddress();
        this.newAddress = newRestaurant.getNewAddress();
        this.lon = newRestaurant.getLon();
        this.lat = newRestaurant.getLat();
        this.lastUpdatedAt = newRestaurant.getLastUpdatedAt();
    }

}
