package com.wemingle.core.domain.category.sports.entity;

import com.wemingle.core.domain.category.sports.entity.sportstype.Sportstype;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class SportsCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK", nullable = false)
    private Long pk;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sprots_name", columnDefinition = "VARCHAR(255) NOT NULL")
    private Sportstype sportsName;
}