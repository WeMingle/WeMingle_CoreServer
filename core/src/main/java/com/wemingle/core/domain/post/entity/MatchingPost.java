package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.applicanttype.ApplicantType;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.univ.CityCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class MatchingPost extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "RECRUITER_ID")
    private Long recruiterId;

    @NotNull
    @Column(name = "content", length = 3000)
    private String content;

    @NotNull
    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @NotNull
    @Column(name = "CAPACITY_LIMIT")
    private int capacityLimit;

    @NotNull
    @Column(name = "COMPLAINTS_COUNT")
    private int complaintsCount;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private CityCode cityCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Ability ability;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruitmentType recruitmentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruiterType recruiterType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ApplicantType applicantType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MatchingStatus matchingStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY")
    private SportsCategory sportsCategory;
}
