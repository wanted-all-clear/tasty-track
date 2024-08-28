package com.allclear.tastytrack.domain.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 맛집 원본 id
    private String mgtno;       // 관리번호
    private String dtlstategbn; // 상세영업코드
    private String bplcnm;      // 사업장명
    private String uptaenm;     // 업태구분명
    private String dcbymd;      // 폐업일자
    private String sitepostno;  // 소재지 우편번호
    private String sitewhladdr; // 지번주소
    private String rdnwhladdr;  // 도로명주소
    private String rdnpostno;   // 도로명 우편번호
    private String lastmodts;   // 최종 수정일자
    private String lon;         // 경도
    private String lat;         // 위도

}
