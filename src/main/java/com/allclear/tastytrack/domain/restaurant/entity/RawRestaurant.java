package com.allclear.tastytrack.domain.restaurant.entity;

import com.allclear.tastytrack.domain.batch.dto.RawRestaurantResponse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RAW_RESTAURANT")
public class RawRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;             // 맛집 원본 id

    @Column(unique = true)
    private String mgtno;       // 관리번호
    private String bplcnm;      // 사업장명
    private String uptaenm;     // 업태구분명
    private String dtlstategbn; // 상세영업상태코드
    private String sitewhladdr; // 지번주소
    private String rdnwhladdr;  // 도로명주소
    private String lon;         // 경도
    private String lat;         // 위도
    private String lastmodts;   // 최종 수정일자

    public void updateWithRawRestaurant(RawRestaurantResponse raw) {

        this.bplcnm = raw.getBplcnm();
        this.uptaenm = raw.getUptaenm();
        this.dtlstategbn = raw.getDtlstategbn();
        this.sitewhladdr = raw.getSitewhladdr();
        this.rdnwhladdr = raw.getRdnwhladdr();
        this.lon = raw.getLon();
        this.lat = raw.getLat();
        this.lastmodts = raw.getLastmodts();
    }

}
