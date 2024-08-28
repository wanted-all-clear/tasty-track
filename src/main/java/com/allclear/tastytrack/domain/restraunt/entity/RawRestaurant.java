package com.allclear.tastytrack.domain.restraunt.entity;

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
    private Long id;

    private String MGTNO;
    private String DTLSTATEGBN;
    private String BPLCNM;
    private String UPTAENM;
    private String DCBYMD;
    private String SITEPOSTNO;
    private String SITEWHLADDR;
    private String RDNWHLADDR;
    private String RDNPOSTNO;
    private String LASTMODTS;
    private String X;
    private String Y;

}
