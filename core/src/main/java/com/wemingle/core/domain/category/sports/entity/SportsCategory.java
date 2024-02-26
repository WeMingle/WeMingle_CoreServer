package com.wemingle.core.domain.category.sports.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class SportsCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK", nullable = false)
    private Long pk;

    @NotNull
    @Column(name = "sprots_name", columnDefinition = "VARCHAR(255) NOT NULL")
    private String sportsName;
}