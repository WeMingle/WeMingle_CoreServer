package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Team extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "TEAM_NAME", columnDefinition = "VARBINARY(255) NOT NULL")
    private String teamName;

    @NotNull
    @Column(name = "CAPACITY_LIMIT")
    private int capacityLimit;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_OWNER")
    private Member teamOwner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPORTS_CATEGORY")
    private SportsCategory sportsCategory;
}
