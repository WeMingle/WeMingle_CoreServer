package com.wemingle.core.domain.category.sports.entity;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
public class SportsCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK", nullable = false)
    private Long pk;

    @Getter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sports_name")
    private SportsType sportsName;
}