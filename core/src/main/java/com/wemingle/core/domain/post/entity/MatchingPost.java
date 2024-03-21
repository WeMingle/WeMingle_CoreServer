package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;

@Getter
@Entity
public class MatchingPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @Column(name = "COMPLETED_MATCHING_CNT")
    private int completedMatchingCnt;

    @NotNull
    @Column(name = "MATCHING_DATE")
    private LocalDate matchingDate;//매칭이 성사된 날짜

    @NotNull
    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;//마감

    @NotNull
    @Column(name = "AREA_NAME")
    private AreaName areaName;

    @NotNull
    @Column(name = "POSITION")
    private Point position;

    @NotNull
    @Column(name = "CONTENT", length = 3000)
    private String content;

    @NotNull
    @Column(name = "CAPACITY_LIMIT")
    private int capacityLimit;

    @NotNull
    @Column(name = "IS_LOCATION_CONSENSUS_POSSIBLE")
    private boolean isLocationConsensusPossible;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Ability ability;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruitmentType recruitmentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruiterType recruiterType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MatchingStatus matchingStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER")
    private TeamMember writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;
}
