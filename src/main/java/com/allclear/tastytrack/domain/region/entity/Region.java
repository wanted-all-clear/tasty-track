package com.allclear.tastytrack.domain.region.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
//@Table(name = "REGION")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String dosi;

    @Column(nullable = false)
    private String sgg;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;


}
