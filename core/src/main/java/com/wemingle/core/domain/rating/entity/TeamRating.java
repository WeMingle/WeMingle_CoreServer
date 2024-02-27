package com.wemingle.core.domain.rating.entity;

import com.wemingle.core.domain.team.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class TeamRating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "TOTAL_RATING")
    private BigDecimal totalRating;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;
}
