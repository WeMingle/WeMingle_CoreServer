package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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
    @Column(name = "CONTENT", length = 3000)
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruitmentType recruitmentType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_OWNER")
    private Member teamOwner;

    @NotNull
    @OneToMany(mappedBy = "team", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TeamMember> teamMembers = new ArrayList<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPORTS_CATEGORY")
    private SportsCategory sportsCategory;

    @Builder
    public Team(String teamName, int capacityLimit, UUID profileImgId, String content, TeamType teamType, RecruitmentType recruitmentType, Member teamOwner, SportsCategory sportsCategory) {
        this.teamName = teamName;
        this.capacityLimit = capacityLimit;
        this.completedMatchingCnt = 0;
        this.profileImgId = profileImgId;
        this.content = content;
        this.teamType = teamType;
        this.recruitmentType = recruitmentType;
        this.teamOwner = teamOwner;
        this.sportsCategory = sportsCategory;
    }

    public void addTeamMember(TeamMember teamMember){
        this.teamMembers.add(teamMember);
    }

    public void addTeamMembers(List<TeamMember> teamMembers){
        this.teamMembers = teamMembers;
    }
}
