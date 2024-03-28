package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "COMPLETED_MATCHING_CNT")
    private int completedMatchingCnt;

    @NotNull
    @Column(name = "PROFILE_IMG_ID", columnDefinition = "VARBINARY(255) NOT NULL")
    private UUID profileImgId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_OWNER")
    private Member teamOwner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPORTS_CATEGORY")
    private SportsCategory sportsCategory;

    @Builder
    public Team(String teamName, int capacityLimit, UUID profileImgId, Member teamOwner, SportsCategory sportsCategory) {
        this.teamName = teamName;
        this.capacityLimit = capacityLimit;
        this.completedMatchingCnt = 0;
        this.profileImgId = profileImgId;
        this.teamOwner = teamOwner;
        this.sportsCategory = sportsCategory;
    }
}
