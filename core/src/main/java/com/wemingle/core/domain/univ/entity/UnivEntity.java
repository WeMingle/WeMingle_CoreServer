package com.wemingle.core.domain.univ.entity;

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

    @Builder
    public UnivEntity(String univName, String domain) {
        this.univName = univName;
        this.domain = domain;
    }
}
