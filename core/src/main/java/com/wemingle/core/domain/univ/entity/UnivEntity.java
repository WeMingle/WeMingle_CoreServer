package com.wemingle.core.domain.univ.entity;

import com.wemingle.core.domain.univ.CityCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "UNIVERSITY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnivEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UNIV_ID", nullable = false)
    private Long univId;

    @Column(name = "UNIV_NAME")
    private String univName;

    @Column(name = "DOMAIN")
    private String domain;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "CITY_CODE")
    private CityCode cityCode;

    @Builder
    public UnivEntity(String univName, String domain, CityCode cityCode) {
        this.univName = univName;
        this.domain = domain;
        this.cityCode = cityCode;
    }
}
