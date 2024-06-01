package com.wemingle.core.domain.team.entity;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.team.dto.TeamDto;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
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

    @Column(name = "START_AGE")
    private int startAge;

    @Column(name = "END_AGE")
    private int endAge;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER")
    private Gender gender;

    @Column(name = "ONLY_SAME_UNIV")
    private boolean onlySameUniv;

    @NotNull
    @Column(name = "PROFILE_IMG_ID", columnDefinition = "VARBINARY(255) NOT NULL")
    private UUID profileImgId;

    @Column(name = "BACKGROUND_IMG_ID")
    private UUID backgroundImgId;

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
    @Enumerated(EnumType.STRING)
    private SportsType sportsCategory;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_OWNER")
    private Member teamOwner;

    @NotNull
    @OneToMany(mappedBy = "team", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TeamMember> teamMembers = new ArrayList<>();

    @Builder
    public Team(String teamName, int capacityLimit, UUID profileImgId, String content, TeamType teamType, RecruitmentType recruitmentType, Member teamOwner, SportsType sportsCategory, int startAge, int endAge, Gender gender, boolean onlySameUniv) {
        this.teamName = teamName;
        this.capacityLimit = capacityLimit;
        this.completedMatchingCnt = 0;
        this.profileImgId = profileImgId;
        this.backgroundImgId = UUID.randomUUID();
        this.content = content;
        this.teamType = teamType;
        this.recruitmentType = recruitmentType;
        this.teamOwner = teamOwner;
        this.sportsCategory = sportsCategory;
        this.startAge = startAge;
        this.endAge = endAge;
        this.gender = gender;
        this.onlySameUniv = onlySameUniv;
    }

    public void addTeamMember(TeamMember teamMember){
        this.teamMembers.add(teamMember);
    }

    public void addTeamMembers(List<TeamMember> teamMembers){
        this.teamMembers = teamMembers;
    }

    public boolean hasGenderCond(){
        return this.gender != null;
    }

    public boolean hasAgeCond(){
        return this.startAge != 0 && this.endAge != 0;
    }
    public void updateTeamSetting(TeamDto.RequestTeamSettingUpdate updateDto){
        this.teamName = updateDto.getTeamName();
        this.content = updateDto.getContent();
        this.recruitmentType = updateDto.getRecruitmentType();
        this.capacityLimit = updateDto.getCapacityLimit();
    }
    public boolean isCapacityLimit() {
        return this.capacityLimit > 0;
    }

    public int getRemainCapacity() {
        final int UNLIMITED = -1;
        return this.isCapacityLimit() ? this.getCapacityLimit() - this.getTeamMembers().size() : UNLIMITED;
    }

    public boolean isAbleToAddMember() {
        return getRemainCapacity() != 0;
    }
}
